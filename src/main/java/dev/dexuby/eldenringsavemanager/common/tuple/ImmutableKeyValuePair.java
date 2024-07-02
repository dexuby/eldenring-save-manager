package dev.dexuby.eldenringsavemanager.common.tuple;

import org.jetbrains.annotations.NotNull;

public interface ImmutableKeyValuePair<K, V> {

    @NotNull
    K getKey();

    @NotNull
    V getValue();

}
