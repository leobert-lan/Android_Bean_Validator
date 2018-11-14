package osp.leobert.android.inspector.sample.bean;

import android.support.annotation.StringDef;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.notations.GenerateValidator;
import osp.leobert.android.inspector.validators.AbsValidator;

/**
 * <p><b>Package:</b> osp.leobert.android.inspector.sample.bean </p>
 * <p><b>Project:</b> Jsr380 </p>
 * <p><b>Classname:</b> Foo </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/11/13.
 */
@GenerateValidator
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
    public String getStr() {
        return str;
    }

    public void setStr(@StringCheck String str) {
        this.str = str;
    }

    public static AbsValidator<Foo> validator(Inspector inspector) {
        return new Inspector.Builder().add(GenerateValidator.FACTORY)
                .build().validator(Foo.class);
    }
}
