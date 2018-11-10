package osp.leobert.android.inspector.validators;

import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.Types;
import osp.leobert.android.inspector.ValidationException;

/**
 * Validates collections.
 */
public class CollectionValidator<C extends Collection<T>, T> extends AbsValidator<C> {
    public static final AbsValidator.Factory FACTORY = new AbsValidator.Factory() {
        @Override
        public @Nullable
        AbsValidator<?> create(Type type,
                               Set<? extends Annotation> annotations,
                               Inspector inspector) {
            Class<?> rawType = Types.getRawType(type);
            if (!annotations.isEmpty()) return null;
            if (rawType == List.class || rawType == Collection.class || rawType == Set.class) {
                return newCollectionValidator(type, inspector).nullSafe();
            }
            return null;
        }
    };

    private final AbsValidator<T> elementValidator;

    private CollectionValidator(AbsValidator<T> elementValidator) {
        this.elementValidator = elementValidator;
    }

    static <T> AbsValidator<Collection<T>> newCollectionValidator(Type type, Inspector inspector) {
        Type elementType = Types.collectionElementType(type, Collection.class);
        AbsValidator<T> elementValidator = inspector.validator(elementType);
        return new CollectionValidator<>(elementValidator);
    }

    @Override
    public void validate(C validationTarget) throws ValidationException {
        for (T element : validationTarget) {
            elementValidator.validate(element);
        }
    }

    @Override
    public String toString() {
        return elementValidator + ".collection()";
    }
}
