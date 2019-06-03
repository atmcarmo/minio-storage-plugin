package org.jenkinsci.plugins.minio;

import java.io.Serializable;

import io.minio.MinioClient;
import io.minio.errors.InvalidEndpointException;
import io.minio.errors.InvalidPortException;

public final class MinioClientFactory implements Serializable {

	private static final long serialVersionUID = 1L;

	private String serverURL;
	private String accessKey;
	private String secretKey;

	private transient MinioClient minioClient;

	public MinioClientFactory(String serverURL, String accessKey, String secretKey) {
		this.serverURL = serverURL;
		this.accessKey = accessKey;
		this.secretKey = secretKey;
	}

	/**
	 * This method creates and returns a MinioClient if not already created.
	 */
	public MinioClient createClient() {
		if (minioClient == null) {
			try {
				minioClient = new MinioClient(serverURL, accessKey, secretKey);
			} catch (InvalidEndpointException e) {
				// Print the stack if invalid endpoint found
				e.printStackTrace();
			} catch (InvalidPortException e) {
				// Print the stack if invalid endpoint port found
				e.printStackTrace();
			}
		}
		return minioClient;
	}
}
