package osp.leobert.android.inspector.validators;

import javax.annotation.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.ValidationException;

/**
 * <p><b>Package:</b> osp.leobert.android.inspector.validators </p>
 * <p><b>Project:</b> Jsr380 </p>
 * <p><b>Classname:</b> AbsValidator </p>
 * <p><b>Description:</b> abstract validator logic for validate T </p>
 * Created by leobert on 2018/9/12.
 */
public abstract class Validator<T> {
    /**
     * Validates a given {@code t} instance
     *
     * @param t the instance
     * @throws ValidationException upon invalidation
     */
    public abstract void validate(T t) throws ValidationException;

    public final boolean isValid(T validationTarget) {
        try {
            validate(validationTarget);
            return true;
        } catch (ValidationException e) {
            return false;
        }
    }

    /**
     * @return a nullsafe validator that ignores null instances.
     */
    public Validator<T> nullSafe() {
        final Validator<T> delegate = this;
        return new Validator<T>() {
            @Override
            public void validate(T validationTarget) throws ValidationException {
                if (validationTarget != null) {
                    delegate.validate(validationTarget);
                }
            }

            @Override
            public String toString() {
                return delegate + ".nullSafe()";
            }
        };
    }

    public interface Factory {
        /**
         * Attempts to create an adapter for {@code type} annotated with {@code annotations}. This
         * returns the adapter if one was created, or null if this factory isn't capable of creating
         * such an adapter.
         * <p>
         * <p>Implementations may use to {@link Inspector#validator} to compose adapters of other types,
         * or {@link Inspector#nextValidator} to delegate to the underlying adapter of the same type.
         */
        @Nullable
        Validator<?> create(Type type,
                            Set<? extends Annotation> annotations,
                            Inspector inspector);
    }
}
