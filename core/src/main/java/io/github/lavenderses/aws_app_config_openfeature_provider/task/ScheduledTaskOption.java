package io.github.lavenderses.aws_app_config_openfeature_provider.task;

import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;

/**
 * An options to determine the behavior of {@link ScheduledTaskExecutor}.
 */
@Data
@Builder(toBuilder = true)
@ToString(callSuper = true)
public final class ScheduledTaskOption {

    public static final ScheduledTaskOption EMPTY_OPTION = ScheduledTaskOption.builder()
        .delay(Duration.ZERO)
        .build();

    /**
     * Fixed delay of the task({@link ScheduledTask}) interval.
     */
    @NotNull
    @NonNull
    private final Duration delay;
}
