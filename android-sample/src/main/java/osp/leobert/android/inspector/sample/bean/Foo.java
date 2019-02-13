package osp.leobert.android.inspector.sample.bean;

import android.support.annotation.StringDef;
import android.util.Log;

import java.util.Date;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.ValidationException;
import osp.leobert.android.inspector.notations.GenerateValidator;
import osp.leobert.android.inspector.notations.ValidatedBy;
import osp.leobert.android.inspector.notations.ValidationQualifier;
import osp.leobert.android.inspector.validators.Validator;

/*
 * <p><b>Package:</b> osp.leobert.android.inspector.sample.bean </p>
 * <p><b>Project:</b> Jsr380 </p>
 * <p><b>Classname:</b> Foo </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/11/13.
 */
@GenerateValidator
@Foo.FooValidatorBy()
public class Foo {
    @StringDef(StringCheck.bar)
    @interface StringCheck {
        String bar = "bar";
    }

    @StringCheck
    String str;

    public Foo(@StringCheck String str) {
        this.str = str;
    }

    @StringCheck
//    @InspectorIgnored
    public String getStr() {
        return str;
    }

    public void setStr(@StringCheck String str) {
        this.str = str;
    }

    public static int i = 0;

    @ValidatedBy(BarIValidator.class)
    public Date getI() {
        i++;
        if (i % 2 == 0)
            return new Date();
        return null;
    }

    public static Validator<Foo> validator(Inspector inspector) {
        return inspector.validator(Foo.class);
    }

    @ValidationQualifier
    public @interface FooValidatorBy {
    }

    public static class FooValidator extends Validator<Foo> {

        @Override
        public void validate(Foo foo) throws ValidationException {
            Log.e("lmsg", "check foo:" + String.valueOf(foo));
            if (foo == null) return;
            if ("bar".equals(foo.getStr()) && foo.getI() == null)
                throw new ValidationException("#getI() should not return null when #getStr() return 'bar'");
        }
    }
}
