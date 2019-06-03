package org.jenkinsci.plugins.minio;

import io.minio.MinioClient;
import io.minio.errors.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.jenkinsci.plugins.minio.MinioClientFactory;
import org.jenkinsci.remoting.RoleChecker;
import org.kohsuke.stapler.DataBoundConstructor;
import org.xmlpull.v1.XmlPullParserException;

import hudson.FilePath;
import hudson.FilePath.FileCallable;
import hudson.model.TaskListener;
import hudson.remoting.VirtualChannel;

public class MinioUploaderCallable implements FileCallable<Void> {
	private static final long serialVersionUID = 1;

	private MinioClientFactory minioClientFactory;
	/**
	 * Bucket name to store the job artifacts.
	 */
	private String bucketName;

	/**
	 * path represents a file path on a specific agent or the master.
	 */
	private FilePath path;

	/**
	 * The name of the file.
	 */
	private String fileName;

	/**
	 * TaskListener listener needed for reading exceptions.
	 */
	TaskListener listener;

	@DataBoundConstructor
	public MinioUploaderCallable(MinioClientFactory minioClientFactory, String bucketName,
								 FilePath path, String fileName, TaskListener listener) {
		this.minioClientFactory = minioClientFactory;
		this.bucketName = bucketName;
		this.path = path;
		this.fileName = fileName;
		this.listener = listener;
	}

	@Override
	public void checkRoles(RoleChecker checker) throws SecurityException {
		// not implemented
	}

	/**
	 * invoke uses minioClient to upload the Object.
	 */
	@Override
	public Void invoke(File f, VirtualChannel channel) throws IOException,
			InterruptedException {
		InputStream stream = null;
		try {
			stream = new FileInputStream(path.getRemote());
			String contentType = "application/octet-stream";
			MinioClient minioClient = minioClientFactory.createClient();

			// Check if bucket already exists
			boolean bucketFound = minioClient.bucketExists(bucketName);

			// If bucket not present, create bucket
			if (!bucketFound) {
				minioClient.makeBucket(bucketName);
			}

			minioClient.putObject(bucketName, fileName, stream, contentType);
		} catch (InvalidKeyException | InvalidBucketNameException
				| NoSuchAlgorithmException | InsufficientDataException
				| NoResponseException | ErrorResponseException
				| InternalException | InvalidArgumentException
				| XmlPullParserException e) {
			e.printStackTrace(listener.error("Minio error, failed to upload files"));
		} catch (IOException e) {
			e.printStackTrace(listener.error("Communication error, failed to upload files"));
		} catch (RegionConflictException e) {
			e.printStackTrace(listener.error("Minio error, failed to upload files"));
		} finally {
			if (stream != null)
				stream.close();
		}
		return null;
	}
}
