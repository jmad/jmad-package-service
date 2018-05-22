/**
 * Copyright (c) 2018 European Organisation for Nuclear Research (CERN), All Rights Reserved.
 */

package org.jmad.modelpack.service.impl;

import static cern.accsoft.steering.jmad.modeldefs.io.impl.ModelDefinitionUtil.ZIP_FILE_EXTENSION;
import static java.util.Objects.requireNonNull;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.jmad.modelpack.domain.ModelPackageVariant;
import org.jmad.modelpack.service.ModelPackageFileCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cern.accsoft.steering.jmad.modeldefs.io.impl.ModelDefinitionUtil;
import cern.accsoft.steering.jmad.util.StreamUtil;
import cern.accsoft.steering.jmad.util.TempFileUtil;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class ModelPackageFileCacheImpl implements ModelPackageFileCache {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelPackageFileCacheImpl.class);

    private static final String CACHE_SUBDIR = "package-cache";

    private final TempFileUtil tempFileUtil;
    private final File cacheDir;

    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * contains all the files which were are used. The purpose here is to return the same file instances each time, so
     * that we can lock on them for checking if they exist or when writing to them. This way, at least we should be able
     * to avoid concurrency issues within the same instance of this cache.
     * <p>
     * ... Concurrency issues within different processes is another story and is not yet adressed.
     */
    private Map<ModelPackageVariant, File> packageFiles = new HashMap<>();

    public ModelPackageFileCacheImpl(TempFileUtil tempFileUtil) {
        this.tempFileUtil = requireNonNull(tempFileUtil, "tempFileUtil must not be null");
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

                Arrays.stream(cacheDir.listFiles()).filter(f -> ModelDefinitionUtil.isZipFileName(f.getName()))
                        .forEach(this::deleteCacheEntry);
            }
        });
    }

    private boolean deleteCacheEntry(File zipFile) {
        deleteFile(jsonFileFor(zipFile));
        return deleteFile(zipFile);
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

    private void writeMetaInfo(ModelPackageVariant modelPackageVariant) {
        File file = jsonFileFor(zipFileFor(modelPackageVariant));

        try (Writer writer = new FileWriter(file)) {
            gson.toJson(modelPackageVariant, writer);
            LOGGER.info("Successfully stored meta info for packageVariant {} in file {}.", modelPackageVariant, file);
        } catch (IOException e) {
            LOGGER.error("Meta info for packageVariant {} could not be written to file {}.", modelPackageVariant, file,
                    e);
        }
    }

}
