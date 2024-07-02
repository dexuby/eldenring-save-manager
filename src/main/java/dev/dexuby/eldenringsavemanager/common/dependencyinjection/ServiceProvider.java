package dev.dexuby.eldenringsavemanager.common.dependencyinjection;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ServiceProvider {

    <T> void register(@NotNull final T service);

    <T> void register(@NotNull final Class<T> clazz, @NotNull final T service);

    <T> void register(@NotNull final String identifier, @NotNull final T service);

    <T> void register(@NotNull final Class<T> clazz, @NotNull final String identifier, @NotNull final T service);

    @Nullable
    <T> T get(@NotNull final Class<T> clazz);

    @NotNull
    <T> T getOrThrow(@NotNull final Class<T> clazz);

    @Nullable
    <T> T get(@NotNull final Class<T> clazz, @NotNull final String identifier);

    @NotNull
    <T> T getOrThrow(@NotNull final Class<T> clazz, @NotNull final String identifier);

    boolean has(@NotNull final Class<?> clazz);

    boolean has(@NotNull final Class<?> clazz, @NotNull final String identifier);

}
