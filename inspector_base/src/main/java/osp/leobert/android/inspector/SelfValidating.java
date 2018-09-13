package osp.leobert.android.inspector;

import com.sun.istack.internal.Nullable;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Set;

import osp.leobert.android.inspector.validators.AbsValidator;

/**
 * An interface that a given type can implement to let Inspector know that it validates itself.
 * <p>
 * <pre><code>
 *   class Foo implements SelfValidating {
 *      &#64;Override public void validate(Inspector inspector) throws ValidationException {
 *        // Implement custom validation logic here.
 *      }
 *   }
 * </code></pre>
 */
public interface SelfValidating {
    /**
     * Validates this object with whatever custom validation implementation the user wants.
     *
     * @throws ValidationException upon invalidation
     */
    void validate(Inspector inspector) throws ValidationException;

    /**
     * A factory instance for this. This is not considered public API.
     */
    AbsValidator.Factory FACTORY = new AbsValidator.Factory() {

        @Nullable
        @Override
        public AbsValidator<?> create(final Type type,
                                      final Set<? extends Annotation> annotations,
                                      final Inspector inspector) {
            if (SelfValidating.class.isAssignableFrom(Types.getRawType(type))) {
                return new AbsValidator<SelfValidating>() {
                    @Override
                    public void validate(SelfValidating target) throws ValidationException {
                        target.validate(inspector);
                    }

                    @Override
                    public String toString() {
                        return "SelfValidating(" + Types.typeToString(type) + ")";
                    }
                };
            }
            return null;
        }
    };
}
