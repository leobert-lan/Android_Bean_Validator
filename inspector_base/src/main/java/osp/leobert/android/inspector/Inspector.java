package osp.leobert.android.inspector;

import javax.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import osp.leobert.android.inspector.notations.ValidationQualifier;
import osp.leobert.android.inspector.validators.AbsValidator;

import osp.leobert.android.inspector.validators.ArrayValidator;
import osp.leobert.android.inspector.validators.ClassValidator;
import osp.leobert.android.inspector.validators.CollectionValidator;
import osp.leobert.android.inspector.validators.MapValidator;
import osp.leobert.android.inspector.validators.StandardValidators;

public final class Inspector {
    private static final List<AbsValidator.Factory> BUILT_IN_FACTORIES = new ArrayList<>(5);

    static {
        BUILT_IN_FACTORIES.add(StandardValidators.FACTORY);
        BUILT_IN_FACTORIES.add(CollectionValidator.FACTORY);
        BUILT_IN_FACTORIES.add(MapValidator.FACTORY);
        BUILT_IN_FACTORIES.add(ArrayValidator.FACTORY);
        BUILT_IN_FACTORIES.add(SelfValidating.FACTORY);
        BUILT_IN_FACTORIES.add(ClassValidator.FACTORY);
    }

    @SuppressWarnings("ThreadLocalUsage")
    private final ThreadLocal<List<DeferredAdapter<?>>> reentrantCalls = new ThreadLocal<>();
    private final List<AbsValidator.Factory> factories;
    private final Map<Object, AbsValidator<?>> adapterCache = new LinkedHashMap<>();

    Inspector(Builder builder) {
        List<AbsValidator.Factory> factories =
                new ArrayList<>(builder.factories.size() + BUILT_IN_FACTORIES.size());
        factories.addAll(builder.factories);
        factories.addAll(BUILT_IN_FACTORIES);
        this.factories = Collections.unmodifiableList(factories);
    }

    /**
     * Returns a AbsValidator for {@code type}, creating it if necessary.
     */
    public <T> AbsValidator<T> validator(Type type) {
        return validator(type, Util.NO_ANNOTATIONS);
    }

    /**
     * Returns a AbsValidator for {@code type}, creating it if necessary.
     */
    public <T> AbsValidator<T> validator(Class<T> type) {
        return validator(type, Util.NO_ANNOTATIONS);
    }


    /**
     * Returns a AbsValidator for {@code type} with {@code annotationType}, creating it if necessary.
     */
    public <T> AbsValidator<T> validator(Type type, Class<? extends Annotation> annotationType) {
        return validator(type,
                Collections.singleton(Types.createValidationQualifierImplementation(annotationType)));
    }

    /**
     * Returns a AbsValidator for {@code type} and {@code annotations}, creating it if necessary.
     */
    @SuppressWarnings("unchecked") // Factories are required to return only matching AbsValidators.
    public <T> AbsValidator<T> validator(Type type, Set<? extends Annotation> annotations) {
        type = Types.canonicalize(type);

        // If there's an equivalent adapter in the cache, we're done!
        Object cacheKey = cacheKey(type, annotations);
        synchronized (adapterCache) {
            AbsValidator<?> result = adapterCache.get(cacheKey);
            if (result != null) return (AbsValidator<T>) result;
        }

        // Short-circuit if this is a reentrant call.
        List<DeferredAdapter<?>> deferredAdapters = reentrantCalls.get();
        if (deferredAdapters != null) {
            for (DeferredAdapter<?> deferredAdapter : deferredAdapters) {
                if (deferredAdapter.cacheKey == null) {
                    // not ready
                    continue;
                }
                if (deferredAdapter.cacheKey.equals(cacheKey)) {
                    return (AbsValidator<T>) deferredAdapter;
                }
            }
        } else {
            deferredAdapters = new ArrayList<>();
            reentrantCalls.set(deferredAdapters);
        }

        // Prepare for re-entrant calls, then ask each factory to create a type adapter.
        DeferredAdapter<T> deferredAdapter = new DeferredAdapter<>(cacheKey);
        deferredAdapters.add(deferredAdapter);
        try {
            for (AbsValidator.Factory factory : factories) {
                AbsValidator<T> result = (AbsValidator<T>) factory.create(type, annotations, this);
                if (result != null) {
                    deferredAdapter.ready(result);
                    synchronized (adapterCache) {
                        adapterCache.put(cacheKey, result);
                    }
                    return result;
                }
            }
        } finally {
            deferredAdapters.remove(deferredAdapters.size() - 1);
            if (deferredAdapters.isEmpty()) {
                reentrantCalls.remove();
            }
        }

        throw new IllegalArgumentException("No AbsValidator for " + type + " annotated " + annotations);
    }


    /**
     * Returns a AbsValidator for {@code type} and {@code annotations}, always creating a new one and
     * skipping past {@code skipPast} for creation.
     */
    @SuppressWarnings("unchecked") // Factories are required to return only matching AbsValidators.
    public <T> AbsValidator<T> nextAbsValidator(AbsValidator.Factory skipPast,
                                                Type type,
                                                Set<? extends Annotation> annotations) {
        type = Types.canonicalize(type);

        int skipPastIndex = factories.indexOf(skipPast);
        if (skipPastIndex == -1) {
            throw new IllegalArgumentException("Unable to skip past unknown factory " + skipPast);
        }
        for (int i = skipPastIndex + 1, size = factories.size(); i < size; i++) {
            AbsValidator<T> result = (AbsValidator<T>) factories.get(i)
                    .create(type, annotations, this);
            if (result != null) return result;
        }
        throw new IllegalArgumentException("No next AbsValidator for "
                + type
                + " annotated "
                + annotations);
    }

    /**
     * Returns a new builder containing all custom factories used by the current instance.
     */
    public Inspector.Builder newBuilder() {
        int fullSize = factories.size();
        int tailSize = BUILT_IN_FACTORIES.size();
        List<AbsValidator.Factory> customFactories = factories.subList(0, fullSize - tailSize);
        return new Builder().addAll(customFactories);
    }

    /**
     * Returns an opaque object that's equal if the type and annotations are equal.
     */
    private Object cacheKey(Type type, Set<? extends Annotation> annotations) {
        if (annotations.isEmpty()) return type;
        return Arrays.asList(type, annotations);
    }


    public static final class Builder {
        final List<AbsValidator.Factory> factories = new ArrayList<>();

        public <T> Builder add(final Type type, final AbsValidator<T> AbsValidator) {
            if (type == null) throw new IllegalArgumentException("type == null");
            if (AbsValidator == null) throw new IllegalArgumentException("AbsValidator == null");

            return add(new AbsValidator.Factory() {
                @Override
                public @Nullable
                AbsValidator<?> create(Type targetType,
                                       Set<? extends Annotation> annotations,
                                       Inspector inspector) {
                    return annotations.isEmpty() && Util.typesMatch(type, targetType) ? AbsValidator : null;
                }
            });
        }

        public <T> Builder add(final Type type,
                               final Class<? extends Annotation> annotation,
                               final AbsValidator<T> AbsValidator) {
            if (type == null) throw new IllegalArgumentException("type == null");
            if (annotation == null) throw new IllegalArgumentException("annotation == null");
            if (AbsValidator == null) throw new IllegalArgumentException("AbsValidator == null");
            if (!annotation.isAnnotationPresent(ValidationQualifier.class)) {
                throw new IllegalArgumentException(annotation + " does not have @ValidationQualifier");
            }
            if (annotation.getDeclaredMethods().length > 0) {
                throw new IllegalArgumentException("Use AbsValidator.Factory for annotations with elements");
            }

            return add(new AbsValidator.Factory() {
                @Override
                public @Nullable
                AbsValidator<?> create(Type targetType,
                                       Set<? extends Annotation> annotations,
                                       Inspector inspector) {
                    if (Util.typesMatch(type, targetType)
                            && annotations.size() == 1
                            && Util.isAnnotationPresent(annotations, annotation)) {
                        return AbsValidator;
                    }
                    return null;
                }
            });
        }

        public Builder add(AbsValidator.Factory factory) {
            if (factory == null) throw new IllegalArgumentException("factory == null");
            factories.add(factory);
            return this;
        }

        Builder addAll(List<AbsValidator.Factory> factories) {
            this.factories.addAll(factories);
            return this;
        }

        public Inspector build() {
            return new Inspector(this);
        }
    }

    /**
     * Sometimes a type adapter factory depends on its own product; either directly or indirectly.
     * To make this work, we offer this type adapter stub while the final adapter is being computed.
     * When it is ready, we wire this to delegate to that finished adapter.
     * <p>
     * <p>Typically this is necessary in self-referential object models, such as an {@code Employee}
     * class that has a {@code List<Employee>} field for an organization's management hierarchy.
     */
    private static class DeferredAdapter<T> extends AbsValidator<T> {
        @Nullable
        Object cacheKey;
        @Nullable
        private AbsValidator<T> delegate;

        DeferredAdapter(Object cacheKey) {
            this.cacheKey = cacheKey;
        }

        void ready(AbsValidator<T> delegate) {
            this.delegate = delegate;
            this.cacheKey = null;
        }

        @Override
        public void validate(T validationTarget) throws ValidationException {
            if (delegate == null) throw new IllegalStateException("AbsValidator isn't ready");
            delegate.validate(validationTarget);
        }
    }
}
