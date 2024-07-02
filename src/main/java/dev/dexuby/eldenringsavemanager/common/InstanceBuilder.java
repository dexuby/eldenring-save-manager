package dev.dexuby.eldenringsavemanager.common;

import org.jetbrains.annotations.NotNull;

public interface InstanceBuilder<T> {

    @NotNull
    T build();

}
