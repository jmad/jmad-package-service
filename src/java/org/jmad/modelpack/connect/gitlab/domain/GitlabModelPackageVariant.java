package org.jmad.modelpack.connect.gitlab.domain;

import org.jmad.modelpack.domain.ModelPackageVariant;

import java.io.UnsupportedEncodingException;
import java.net.URI;

import static java.net.URLEncoder.encode;

public class GitlabModelPackageVariant extends ModelPackageVariant {
    public GitlabModelPackageVariant(GitlabModelPackage modelPackage, GitlabVariant variant) {
        super(buildGitlabUri(modelPackage, variant), modelPackage, variant);
    }

    private static URI buildGitlabUri(GitlabModelPackage modelPackage, GitlabVariant variant) {
        return URI.create(modelPackage.uri().toASCIIString() + "@" + encodedVariant(variant));
    }

    private static String encodedVariant(GitlabVariant variant) {
        try {
            return encode(variant.name(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
}
