package dev.dexuby.eldenringsavemanager.common.dependencyinjection;

import dev.dexuby.eldenringsavemanager.common.FluentInstanceBuilder;
import dev.dexuby.eldenringsavemanager.common.tuple.ImmutableKeyValuePair;
import dev.dexuby.eldenringsavemanager.common.tuple.ImmutablePair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class InstanceServiceProvider implements ServiceProvider {

    private final Map<ServiceIdentifier, Object> services = new HashMap<>();

    private InstanceServiceProvider(@NotNull final InstanceServiceProvider.Builder builder) {

        if (builder.selfService) {
            this.register(this);
            this.register(ServiceProvider.class, this);
        }

        builder.services.forEach(service -> this.services.put(service.getKey(), service.getValue()));

    }

    @Override
    public <T> void register(@NotNull final T service) {

        this.services.put(ServiceIdentifier.of(service.getClass()), service);

    }

    @Override
    public <T> void register(@NotNull final Class<T> clazz, @NotNull final T service) {

        this.services.put(ServiceIdentifier.of(clazz), service);

    }

    @Override
    public <T> void register(@NotNull final String identifier, @NotNull final T service) {

        this.services.put(ServiceIdentifier.of(service.getClass(), identifier), service);

    }

    @Override
    public <T> void register(@NotNull final Class<T> clazz, @NotNull final String identifier, @NotNull final T service) {

        this.services.put(ServiceIdentifier.of(clazz, identifier), service);

    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T get(@NotNull final Class<T> clazz) {

        return (T) this.services.get(ServiceIdentifier.of(clazz));

    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T> T getOrThrow(@NotNull final Class<T> clazz) {

        final ServiceIdentifier serviceIdentifier = ServiceIdentifier.of(clazz);
        final T service = (T) this.services.get(serviceIdentifier);
        if (service == null)
            throw new IllegalArgumentException("No service found for class " + clazz.getName());

        return service;

    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T get(@NotNull final Class<T> clazz, @NotNull final String identifier) {

        return (T) this.services.get(ServiceIdentifier.of(clazz, identifier));

    }

    @SuppressWarnings("unchecked")
    @NotNull
    @Override
    public <T> T getOrThrow(@NotNull final Class<T> clazz, @NotNull final String identifier) {

        final ServiceIdentifier serviceIdentifier = ServiceIdentifier.of(clazz, identifier);
        final T service = (T) this.services.get(serviceIdentifier);
        if (service == null)
            throw new IllegalArgumentException("No service found for class " + clazz.getName());

        return service;

    }

    @Override
    public boolean has(@NotNull final Class<?> clazz) {

        return this.services.containsKey(ServiceIdentifier.of(clazz));

    }

    @Override
    public boolean has(@NotNull final Class<?> clazz, @NotNull final String identifier) {

        return this.services.containsKey(ServiceIdentifier.of(clazz, identifier));

    }

    public static InstanceServiceProvider.Builder builder() {

        return new InstanceServiceProvider.Builder();

    }

    public static class Builder implements FluentInstanceBuilder<InstanceServiceProvider> {

        private final List<ImmutableKeyValuePair<ServiceIdentifier, Object>> services = new ArrayList<>();
        private boolean selfService = false;

        @NotNull
        public <T> Builder service(@NotNull final T instance) {

            this.services.add(ImmutablePair.of(ServiceIdentifier.of(instance.getClass()), instance));
            return this;

        }

        @NotNull
        public <T> Builder service(@NotNull final Class<T> clazz, @NotNull final T instance) {

            this.services.add(ImmutablePair.of(ServiceIdentifier.of(clazz), instance));
            return this;

        }

        @NotNull
        public <T> Builder service(@NotNull final String identifier, @NotNull final T instance) {

            this.services.add(ImmutablePair.of(ServiceIdentifier.of(instance.getClass(), identifier), instance));
            return this;

        }

        @NotNull
        public <T> Builder service(@NotNull final Class<T> clazz, @NotNull final String identifier, @NotNull final T instance) {

            this.services.add(ImmutablePair.of(ServiceIdentifier.of(clazz, identifier), instance));
            return this;

        }

        @NotNull
        public Builder selfService() {

            this.selfService = true;
            return this;

        }

        @NotNull
        @Override
        public InstanceServiceProvider build() {

            return new InstanceServiceProvider(this);

        }

    }

}
