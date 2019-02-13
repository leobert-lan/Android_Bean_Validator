package osp.leobert.android.inspector.validators;

import org.junit.Assert;
import org.junit.Test;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.ValidationException;
import osp.leobert.android.inspector.notations.ValidatedBy;

/**
 * <p><b>Package:</b> osp.leobert.android.inspector.validators </p>
 * <p><b>Project:</b> Android_Bean_Validator </p>
 * <p><b>Classname:</b> ClassValidatorTest </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2019/1/31.
 */
public class ClassValidatorTest {
    public static class Foo {

        @ValidatedBy(BarValidator.class)
        public Integer getFoo() {
            return 0;
        }
    }

    public static class BarValidator extends Validator<Integer> {

        @Override
        public void validate(Integer integer) throws ValidationException {

        }
    }

    Inspector inspector;

    @org.junit.Before
    public void setUp() throws Exception {
        inspector = new Inspector.Builder()
                .add(ClassValidator.FACTORY)
                .build();
    }

    @Test
    public void name() {

        boolean isValid = inspector.validator(Foo.class)
                .isValid(new Foo());
        Assert.assertTrue(isValid);
    }
}