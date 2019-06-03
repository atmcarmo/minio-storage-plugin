package org.jenkinsci.plugins.minio.pipeline;

import com.google.common.collect.ImmutableSet;
import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.minio.MinioUploaderEntity;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;
import java.util.Set;
import java.util.logging.Logger;

public class MinioUploaderPipelineStep extends Step implements Serializable {
    private static final Logger LOG = Logger.getLogger(Step.class.getName());

    private MinioUploaderEntity minioUploaderEntity;

    @DataBoundConstructor
    public MinioUploaderPipelineStep(String sourceFile, String excludedFile, String bucketName, String objectNamePrefix) {
        this.minioUploaderEntity = new MinioUploaderEntity(sourceFile, excludedFile, bucketName, objectNamePrefix);
    }

    @Override
    public StepExecution start(StepContext stepContext) throws Exception {
        return new MinioUploaderPipelineStepExecution(this, stepContext);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor implements Serializable {

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return ImmutableSet.of(FilePath.class, TaskListener.class, Launcher.class);
        }

        @Override
        public String getFunctionName() {
            return "minioUpload";
        }
    }

    public MinioUploaderEntity getMinioUploaderEntity() {
        return minioUploaderEntity;
    }

}
