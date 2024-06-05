package io.github.lavenderses.aws_app_config_openfeature_provider.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

/**
 * A class to mask secret string to avoid it appeared in logging or something.
 * This works completely exactly except {@code toString} method. For example, comparison or equality.
 */
@Getter
@AllArgsConstructor
@Builder(toBuilder = true)
public final class Credential implements Comparable<Credential> {

    @NotNull @NonNull private final String rawValue;

    @Override
    public String toString() {
        return "Masked credentials(***)";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Credential credential) {
            return rawValue.equals(credential.rawValue);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return rawValue.hashCode();
    }

    @Override
    public int compareTo(@NotNull Credential o) {
        return rawValue.compareTo(o.rawValue);
    }
}
