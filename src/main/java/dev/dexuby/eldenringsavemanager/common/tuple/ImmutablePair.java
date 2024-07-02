package dev.dexuby.eldenringsavemanager.common.tuple;

import dev.dexuby.eldenringsavemanager.common.conditional.Preconditions;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class ImmutablePair<K, V> implements ImmutableKeyValuePair<K, V> {

    private final K key;
    private final V value;

    public ImmutablePair(@NotNull final K key,
                         @NotNull final V value) {

        Preconditions.checkNotNull(key, value);

        this.key = key;
        this.value = value;

    }

    @NotNull
    @Override
    public K getKey() {

        return this.key;

    }

    @NotNull
    @Override
    public V getValue() {

        return this.value;

    }

    @Override
    public int hashCode() {

        return Objects.hash(key, value);

    }

    @Override
    public boolean equals(final Object input) {

        if (this == input) return true;
        if (input == null || getClass() != input.getClass()) return false;

        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) input;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);

    }

    public static <K, V> ImmutablePair<K, V> of(@NotNull final K key, @NotNull final V value) {

        return new ImmutablePair<>(key, value);

    }

}
