package io.github.lavenderses.aws_app_config_openfeature_provider.task;

/**
 * An interface for the task which should be executed infinitely.
 * To execute it, pass this task to {@link ScheduledTaskExecutor#start(ScheduledTask)}.
 * <br/>
 * Note that this task should execute single processing task, no need to loop infinitely.
 */
public interface ScheduledTask {

    void run();
}
