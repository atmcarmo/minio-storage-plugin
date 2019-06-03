package org.jenkinsci.plugins.minio.pipeline;

import hudson.FilePath;
import hudson.Launcher;
import hudson.model.Run;
import hudson.model.TaskListener;
import org.jenkinsci.plugins.minio.MinioUploader;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.SynchronousNonBlockingStepExecution;

import javax.annotation.Nonnull;
import java.io.Serializable;

public class MinioUploaderPipelineStepExecution extends SynchronousNonBlockingStepExecution<Void> implements Serializable {
    MinioUploaderPipelineStep step;

    public MinioUploaderPipelineStepExecution(@Nonnull MinioUploaderPipelineStep step, StepContext context) {
        super(context);
        this.step = step;
    }

    @Override
    protected Void run() throws Exception {
        Launcher launcher = this.getContext().get(Launcher.class);
        Run run = this.getContext().get(Run.class);
        TaskListener listener = this.getContext().get(TaskListener.class);
        FilePath ws = this.getContext().get(FilePath.class);

        new MinioUploader(this.step.getMinioUploaderEntity()).perform(run, ws, launcher, listener);

        return null;
    }
}
