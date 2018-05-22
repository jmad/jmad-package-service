/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.impl;

import static cern.accsoft.steering.jmad.modeldefs.io.impl.ModelDefinitionUtil.ZIP_FILE_EXTENSION;
import static cern.accsoft.steering.jmad.modeldefs.io.impl.ModelDefinitionUtil.isZipFileName;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.domain.ModelPackageVariantImpl;
import org.jmad.modelpack.service.ModelPackageFileCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cern.accsoft.steering.jmad.util.StreamUtil;
import cern.accsoft.steering.jmad.util.TempFileUtil;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ModelPackageFileCacheImpl implements ModelPackageFileCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelPackageFileCacheImpl.class);
    private static final String CACHE_SUBDIR = "package-cache";

    private final File cacheDir;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * contains all the files which were are used. The purpose here is to return the same file instances each time, so
     * that we can lock on them for checking if they exist or when writing to them. This way, at least we should be able
     * to avoid concurrency issues within the same instance of this cache.
     * <p>
     * ... Concurrency issues within different processes is another story and is not yet addressed.
     */
    private Map<ModelPackageVariant, File> packageFiles = new HashMap<>();

    public ModelPackageFileCacheImpl(TempFileUtil tempFileUtil) {
        requireNonNull(tempFileUtil, "tempFileUtil must not be null");
        this.cacheDir = tempFileUtil.getOutputFile(CACHE_SUBDIR);
    }

    @Override
    public Mono<File> fileFor(ModelPackageVariant packageVariant,
            Function<ModelPackageVariant, Mono<Resource>> zipFileResourceCallback) {
        File packageFile = packageFile(packageVariant);
        synchronized (packageFile) {
            if (packageFile.exists()) {
                return Mono.just(packageFile);
            }

            // @formatter:off
            return Mono.just(packageVariant)
                .publishOn(Schedulers.elastic())
                .doOnNext(v -> LOGGER.info("Downloading model package {} to temp file {}.", v, packageFile))
                .flatMap(zipFileResourceCallback)
                .map(r -> {
                        synchronized (packageFile) {
                            return downloadFile(packageVariant, r, packageFile);
                        }
                    });
            // @formatter:on
        }
    }

    @Override
    public Flux<ModelPackageVariant> cachedPackageVariants() {
        // @formatter:off
        return Flux.fromIterable(existingJsonFiles())
                    .map(this::readMetaInfoFrom)
                    .filter(Optional::isPresent)
                    .map(Optional::get);
        // @formatter:on
    }

    private File downloadFile(ModelPackageVariant packageVariant, Resource zipResource, File file) {
        try {
            LOGGER.info("Storing model package {} to temp file {}.", packageVariant, file.getAbsoluteFile());
            StreamUtil.toFile(zipResource.getInputStream(), file);
            LOGGER.info("Successfully stored model package to file {}.", file.getAbsoluteFile());
            writeMetaInfo(packageVariant);
            return file;
        } catch (IOException e) {
            throw new IllegalStateException("Unable to download package file for package '" + packageVariant + "'");
        }
    }

    File packageFile(ModelPackageVariant packageVariant) {
        synchronized (packageFiles) {
            File file = packageFiles.get(packageVariant);
            if (file != null) {
                return file;
            }
            file = zipFileFor(packageVariant);
            packageFiles.put(packageVariant, file);
            return file;
        }
    }

    private File zipFileFor(ModelPackageVariant packageVariant) {
        return new File(cacheDir, zipFileName(packageVariant));
    }

    private static final String zipFileName(ModelPackageVariant packageVariant) {
        return packageVariant.fullName() + ZIP_FILE_EXTENSION;
    }

    private File jsonFileFor(File zipFile) {
        return new File(zipFile.getAbsolutePath() + ".json");
    }

    @Override
    public Mono<Void> clear() {
        return Mono.fromRunnable(() -> {
            synchronized (packageFiles) {
                Set<ModelPackageVariant> deletedKeys = new HashSet<>();
                packageFiles.entrySet().forEach(e -> {
                    File file = e.getValue();

                    synchronized (file) {
                        if (deleteCacheEntry(file)) {
                            deletedKeys.add(e.getKey());
                        }
                    }
                });
                deletedKeys.stream().forEach(packageFiles::remove);

                /* Also try to remove the rest of the files, even if they were not in the map */
                cachedZipFiles().stream().forEach(this::deleteCacheEntry);
            }
        });
    }

    private Set<File> cachedZipFiles() {
        // @formatter:off
        return Arrays.stream(cacheDir.listFiles())
                .filter(f -> isZipFileName(f.getName()))
                .collect(toSet());
        // @formatter:on
    }

    private boolean deleteCacheEntry(File zipFile) {
        deleteFile(jsonFileFor(zipFile));
        return deleteFile(zipFile);
    }

    private Set<File> existingJsonFiles() {
        // @formatter:off
       return cachedZipFiles().stream()
               .map(this::jsonFileFor)
               .filter(File::exists)
               .collect(Collectors.toSet());
        // @formatter:on
    }

    private boolean deleteFile(File file) {
        try {
            Files.delete(file.toPath());
            LOGGER.info("Deleted file {}.", file);
            return true;
        } catch (IOException e) {
            LOGGER.warn("File {} could not be deleted.", file, e);
            return false;
        }
    }

    private void writeMetaInfo(ModelPackageVariant packageVariant) {
        File file = jsonFileFor(zipFileFor(packageVariant));

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(packageVariant, writer);
            LOGGER.info("Successfully stored meta info for packageVariant {} in file {}.", packageVariant, file);
        } catch (IOException e) {
            LOGGER.error("Meta info for packageVariant {} could not be written to file {}.", packageVariant, file, e);
        }
    }

    private Optional<ModelPackageVariant> readMetaInfoFrom(File jsonFile) {
        try (Reader writer = new FileReader(jsonFile)) {
            ModelPackageVariantImpl packageVariant = gson.fromJson(writer, ModelPackageVariantImpl.class);
            LOGGER.info("Successfully read meta info for packageVariant {} from file {}.", packageVariant, jsonFile);
            return Optional.of(packageVariant);
        } catch (IOException e) {
            LOGGER.error("Meta info could not be read from file {}.", jsonFile, e);
            return Optional.empty();
        }
    }

}
