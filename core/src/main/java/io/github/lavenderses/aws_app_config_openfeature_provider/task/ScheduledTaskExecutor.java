package io.github.lavenderses.aws_app_config_openfeature_provider.task;

import jakarta.annotation.PreDestroy;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.util.Objects.requireNonNull;

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

    @NotNull
    private final ScheduledExecutorService executorService;

    @NotNull
    private final ScheduledTaskOption option;

    @VisibleForTesting
    ScheduledTaskExecutor(
        @NotNull final ScheduledExecutorService executorService,
        @NotNull final ScheduledTaskOption option
    ) {
        this.executorService = requireNonNull(executorService, "executorService");
        this.option = requireNonNull(option, "scheduledTaskOption");
    }

    public ScheduledTaskExecutor(
        @NotNull final ScheduledTaskOption option
    ) {
        executorService = Executors.newScheduledThreadPool(1);

        this.option = requireNonNull(option, "scheduledTaskOption");
    }

    public void start(
        @NotNull final ScheduledTask execution
    ) {
        executorService.scheduleWithFixedDelay(
            /* command = */ execution::run,
            /* initialDelay = */ 0,
            /* delay = */ option.getDelay().getSeconds(),
            /* unit = */ TimeUnit.SECONDS
        );
    }

    @PreDestroy
    @Override
    public void close() throws InterruptedException {
        log.info("Start termination of cache update scheduled task for {} second", TERMINATION_WAIT_SECOND);
        executorService.shutdown();

        final boolean terminated = executorService.awaitTermination(
            /* timeout = */ TERMINATION_WAIT_SECOND,
            /* unit = */ TimeUnit.SECONDS
        );

        if (terminated) {
            log.info("Terminated of cache update scheduled task");
        } else {
            log.error(
                "Timed out {} second for termination of cache update scheduled task. Start force termination",
                TERMINATION_WAIT_SECOND
            );

            executorService.shutdownNow();
        }
    }
}
