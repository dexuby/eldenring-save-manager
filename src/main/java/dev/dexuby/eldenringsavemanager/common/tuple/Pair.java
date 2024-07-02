package dev.dexuby.eldenringsavemanager.common.tuple;

import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class Pair<K, V> implements KeyValuePair<K, V> {

    private K key;
    private V value;

    public Pair(@Nullable final K key,
                @Nullable final V value) {

        this.key = key;
        this.value = value;

    }

    @Nullable
    @Override
    public K getKey() {

        return this.key;

    }

    public void setKey(@Nullable final K key) {

        this.key = key;

    }

    @Nullable
    @Override
    public V getValue() {

        return this.value;

    }

    public void setValue(@Nullable final V value) {

        this.value = value;

    }

    @Override
    public int hashCode() {

        return Objects.hash(key, value);

    }

    @Override
    public boolean equals(final Object input) {

        if (this == input) return true;
        if (input == null || getClass() != input.getClass()) return false;

        Pair<?, ?> that = (Pair<?, ?>) input;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);

    }

    public static <K, V> Pair<K, V> of(@Nullable final K key, @Nullable final V value) {

        return new Pair<>(key, value);

    }

}
