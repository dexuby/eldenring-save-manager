package dev.dexuby.eldenringsavemanager.common.tuple;

import org.jetbrains.annotations.Nullable;

public interface KeyValuePair<K, V> {

    @Nullable
    K getKey();

    void setKey(@Nullable final K key);

    @Nullable
    V getValue();

    void setValue(@Nullable final V value);

}
