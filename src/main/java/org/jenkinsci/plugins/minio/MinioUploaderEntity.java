package org.jenkinsci.plugins.minio;

import java.io.Serializable;

public final class MinioUploaderEntity implements Serializable {

	/**
	 * File name relative to the workspace root to upload. Can contain macros
	 * and wildcards.
	 */
	private String sourceFile;

	/**
	 * File name relative to the workspace root to be excluded from upload. Can
	 * contain macros and wildcards.
	 */
	private String excludedFile;

	/**
	 * Bucket name to store the job artifacts.
	 */
	private String bucketName;

	/**
	 * Prefix to be added to the object name before upload.
	 */
	private String objectNamePrefix;

	public MinioUploaderEntity(String sourceFile, String excludedFile, String bucketName, String objectNamePrefix) {
		this.sourceFile = sourceFile;
		this.excludedFile = excludedFile;
		this.bucketName = bucketName;
		this.objectNamePrefix = objectNamePrefix;
	}

	public String getSourceFile() {
		return sourceFile;
	}

	public String getExcludedFile() {
		return excludedFile;
	}

	public String getBucketName() {
		return bucketName;
	}

	public String getObjectNamePrefix() {
		return objectNamePrefix;
	}

}
