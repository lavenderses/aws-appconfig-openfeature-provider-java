package io.github.lavenderses.aws_app_config_openfeature_provider.task;

import static java.util.Objects.requireNonNull;

import io.github.lavenderses.aws_app_config_openfeature_provider.exception.ScheduledTaskExecutorCloseException;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Internal library to execute infinite task (which implements {@link ScheduledTask}).
 * This class manages followings.
 * <ul>
 *     <li>Task execution interval</li>
 *     <li>Task execution resources (like thread)</li>
 *     <li>Safe resource close</li>
 * </ul>
 */
public final class ScheduledTaskExecutor implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTaskExecutor.class);
    private static final long TERMINATION_WAIT_SECOND = 60 * 5;

    @NotNull private final ScheduledExecutorService executorService;

    @NotNull private final ScheduledTaskOption option;

    @VisibleForTesting
    ScheduledTaskExecutor(
            @NotNull final ScheduledExecutorService executorService,
            @NotNull final ScheduledTaskOption option) {
        this.executorService = requireNonNull(executorService, "executorService");
        this.option = requireNonNull(option, "scheduledTaskOption");
    }

    public ScheduledTaskExecutor(@NotNull final ScheduledTaskOption option) {
        executorService = Executors.newScheduledThreadPool(1);

        this.option = requireNonNull(option, "scheduledTaskOption");
    }

    public void start(@NotNull final ScheduledTask execution) {
        executorService.scheduleWithFixedDelay(
                /* command= */ execution::run,
                /* initialDelay= */ 0,
                /* delay= */ option.getDelay().getSeconds(),
                /* unit= */ TimeUnit.SECONDS);
    }

    @PreDestroy
    @Override
    public void close() throws InterruptedException {
        final String taskName = option.getTaskName();
        log.info("Start termination of task[{}] for {} second", TERMINATION_WAIT_SECOND, taskName);
        executorService.shutdown();

        boolean terminated =
                executorService.awaitTermination(
                        /* timeout= */ TERMINATION_WAIT_SECOND, /* unit= */ TimeUnit.SECONDS);

        if (terminated) {
            log.info("Terminated of task[{}]", taskName);
        } else {
            log.error(
                    "Timed out {} second for termination of task[{}]. Start force termination",
                    TERMINATION_WAIT_SECOND,
                    taskName);

            executorService.shutdownNow();

            terminated =
                    executorService.awaitTermination(
                            /* timeout= */ TERMINATION_WAIT_SECOND, /* unit= */ TimeUnit.SECONDS);

            if (terminated) {
                log.warn("Terminated of task[{}] with force shutdown", taskName);
            } else {
                throw new ScheduledTaskExecutorCloseException(
                        "Failed to terminate task[%s]".formatted(taskName));
            }
        }
    }
}
