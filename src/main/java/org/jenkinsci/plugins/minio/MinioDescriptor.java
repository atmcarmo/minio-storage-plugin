package org.jenkinsci.plugins.minio;

import hudson.Extension;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Publisher;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Descriptor for {@link MinioDescriptor}. Used as a singleton. The class is
 * marked as public so that it can be accessed from views.
 *
 * <p>
 * See
 * <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
 * for the actual HTML fragment for the configuration screen.
 */
public class MinioDescriptor extends BuildStepDescriptor<Publisher> {
	/**
	 * To persist global configuration information, simply store it in a
	 * field and call save().
	 *
	 * <p>
	 * If you don't want fields to be persisted, use <tt>transient</tt>.
	 */
	private String serverURL;
	private String accessKey;
	private String secretKey;

	/**
	 * load the persisted global configuration.
	 */
	public MinioDescriptor() {
		load();
	}

	public boolean isApplicable(Class<? extends AbstractProject> aClass) {
		// Indicates that this builder can be used with all kinds of project
		// types
		return true;
	}

	/**
	 * This human readable name is used in the configuration screen.
	 */
	public String getDisplayName() {
		return "Upload build artifacts to Minio server";
	}

	@Override
	public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
		// To persist global configuration information,
		// set that to properties and call save().
		serverURL = formData.getString("serverURL");
		accessKey = formData.getString("accessKey");
		secretKey = formData.getString("secretKey");

		save();
		return super.configure(req, formData);
	}

	/**
	 * This method returns server URL from global configuration.
	 *
	 * @return Returns the Minio Server URL
	 */
	public String getServerURL() {
		return serverURL;
	}

	/**
	 * This method returns Access Key from global configuration.
	 *
	 * @return Returns the Access Key for Minio Server
	 */
	public String getAccessKey() {
		return accessKey;
	}

	/**
	 * This method returns Secret Key from global configuration.
	 *
	 * @return Returns the Secret Key for Minio Server
	 */
	public String getSecretKey() {
		return secretKey;
	}

}
