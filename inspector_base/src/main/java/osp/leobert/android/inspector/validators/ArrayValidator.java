package osp.leobert.android.inspector.validators;

import javax.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.Set;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.Types;
import osp.leobert.android.inspector.ValidationException;


/**
 * Validates arrays.
 */
public final class ArrayValidator extends AbsValidator<Object> {
    public static final Factory FACTORY = new Factory() {
        @Override
        public @Nullable
        AbsValidator<?> create(Type type,
                               Set<? extends Annotation> annotations,
                               Inspector inspector) {
            Type elementType = Types.arrayComponentType(type);
            if (elementType == null) return null;
            if (!annotations.isEmpty()) return null;
            AbsValidator<Object> elementAbsValidator = inspector.validator(elementType);
            return new ArrayValidator(elementAbsValidator).nullSafe();
        }
    };

    private final AbsValidator<Object> elementAbsValidator;

    ArrayValidator(AbsValidator<Object> elementAbsValidator) {
        this.elementAbsValidator = elementAbsValidator;
    }

    @Override
    public void validate(Object validationTarget) throws ValidationException {
        for (int i = 0, size = Array.getLength(validationTarget); i < size; i++) {
            elementAbsValidator.validate(Array.get(validationTarget, i));
        }
    }

    @Override
    public String toString() {
        return elementAbsValidator + ".array()";
    }
}
