package dev.dexuby.eldenringsavemanager.common.conditional;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Preconditions {

    public static <T> void checkNotNull(@Nullable final T reference) throws NullPointerException {

        if (reference == null)
            throw new NullPointerException("Reference was null.");

    }

    @SafeVarargs
    public static <T> void checkNotNull(@Nullable final T... references) throws NullPointerException {

        for (int i = 0; i < references.length; i++) {
            final T reference = references[i];
            if (reference == null)
                throw new NullPointerException("Reference at index " + i + " was null.");
        }

    }

    public static <T> void checkNull(@Nullable final T reference) throws IllegalArgumentException {

        if (reference != null)
            throw new IllegalArgumentException("Reference was not null.");

    }

    @SafeVarargs
    public static <T> void checkNull(@Nullable final T... references) throws IllegalArgumentException {

        for (int i = 0; i < references.length; i++) {
            final T reference = references[i];
            if (reference != null)
                throw new IllegalArgumentException("Reference at index " + i + " was not null.");
        }

    }

    public static void checkState(final boolean state) throws IllegalStateException {

        if (!state)
            throw new IllegalStateException("State was false.");

    }

    private Preconditions() {

        throw new UnsupportedOperationException();

    }

}
