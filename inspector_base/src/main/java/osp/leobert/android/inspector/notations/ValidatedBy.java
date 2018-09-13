package osp.leobert.android.inspector.notations;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import osp.leobert.android.inspector.validators.AbsValidator;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Retention(RUNTIME)
@Target(METHOD)
public @interface ValidatedBy {
    /**
     * @return an array of one or more {@link AbsValidator} classes.
     */
    Class<? extends AbsValidator<?>>[] value();
}
