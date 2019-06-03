package org.jenkinsci.plugins.minio;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.Util;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.Recorder;
import jenkins.tasks.SimpleBuildStep;
import org.apache.commons.lang.StringUtils;
import org.kohsuke.stapler.DataBoundConstructor;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

public final class MinioUploader extends Recorder implements SimpleBuildStep {

	private MinioUploaderEntity minioUploaderEntity;

	// Fields in config.jelly must match the parameter names in the "DataBoundConstructor"
	@DataBoundConstructor
	public MinioUploader(String sourceFile, String excludedFile, String bucketName, String objectNamePrefix) {
		this.minioUploaderEntity = new MinioUploaderEntity(sourceFile, excludedFile, bucketName, objectNamePrefix);
	}

	public MinioUploader(MinioUploaderEntity minioUploaderEntity) {
		this.minioUploaderEntity = minioUploaderEntity;
	}

	private void log(final PrintStream logger, final String message) {
		logger.println(StringUtils.defaultString(getDescriptor().getDisplayName()) + ' ' + message);
	}

	@Override
	public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath ws, @Nonnull Launcher launcher,
						@Nonnull TaskListener listener) {
		final PrintStream console = listener.getLogger();

		if (Result.ABORTED.equals(run.getResult())) {
			// build aborted. don't upload
			log(console, "Skipping publishing on Minio because build aborted");
			return;
		}

		if (Result.FAILURE.equals(run.getResult())) {
			// build failed. don't upload
			log(console, "Skipping publishing on Minio because build failed");
			return;
		}

		try {
			// Get the environment variables - sourceFile to upload, file to not
			// upload
			final Map<String, String> envVars = run.getEnvironment(listener);

			String serverURL = getDescriptor().getServerURL();
			String accessKey = getDescriptor().getAccessKey();
			String secretKey = getDescriptor().getSecretKey();
			MinioClientFactory minioClientFactory = new MinioClientFactory(serverURL, accessKey, secretKey);
			//MinioClient minioClient = minioClientFactory.createClient();

			final String expanded = Util.replaceMacro(this.minioUploaderEntity.getSourceFile(), envVars);
			final String exclude = Util.replaceMacro(this.minioUploaderEntity.getExcludedFile(), envVars);

			// If no files matching, throw IOException
			if (expanded == null) {
				throw new IOException();
			}

			// For each of the file paths provided by the user
			for (String startPath : expanded.split(",")) {
				// Find the files matching the path
				for (FilePath path : ws.list(startPath, exclude)) {

					// Throw IOException if the path is a directory
					if (path.isDirectory()) {
						throw new IOException(path + " is a directory");
					}

					// Get the search path length
					final int searchPathLength = FileHelper.getSearchPathLength(ws.getRemote(), startPath.trim());

					// Get the exact filename to be uploaded
					String fileName = path.getName();
					String objectName;

					// Check if a prefix is setup, prepend to the filename
					if ((this.minioUploaderEntity.getObjectNamePrefix() != null) && !(this.minioUploaderEntity.getObjectNamePrefix().isEmpty())) {
						String[] pathItems = fileName.split("/");
						String name = pathItems[pathItems.length - 1];
						objectName = String.format("%s/%s", this.minioUploaderEntity.getObjectNamePrefix(), name);
					} else {
						objectName = fileName;
					}

					// upload the file from slave/master.
					path.act(new MinioUploaderCallable(minioClientFactory, this.minioUploaderEntity.getBucketName(), path, objectName, listener));

					String msg = String.format("\nFile %s, is uploaded to bucket %s as %s", fileName, this.minioUploaderEntity.getBucketName(),
							objectName);
					log(console, msg);
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace(listener.error("Communication error, failed to upload files"));
			run.setResult(Result.UNSTABLE);
		} catch (InterruptedException e) {
			e.printStackTrace(listener.error("Upload interrupted, failed to upload files"));
			run.setResult(Result.UNSTABLE);
		}
	}

	// Overridden for better type safety.
	// If your plugin doesn't really define any property on Descriptor,
	// you don't have to do this.
	@Override
	public DescriptorImpl getDescriptor() {
		return (DescriptorImpl) super.getDescriptor();
	}


	@Extension
	// This indicates to Jenkins that this is an implementation of an extension
	// point.
	public final static class DescriptorImpl extends MinioDescriptor {

		public DescriptorImpl() {
			super();
		}
	}
}
