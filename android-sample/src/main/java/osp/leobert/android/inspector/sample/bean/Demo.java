package osp.leobert.android.inspector.sample.bean;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.notations.GenerateValidator;
import osp.leobert.android.inspector.validators.AbsValidator;


/**
 * <p><b>Package:</b> io.sweers.inspector.sample </p>
 * <p><b>Project:</b> inspector-root </p>
 * <p><b>Classname:</b> Demo </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/11/13.
 */
@GenerateValidator
public class Demo {
    @NonNull
    private String s;

    @IntRange(from = 0)
    private int foo;

    @NonNull
    public String getS() {
        return s;
    }

    public void setS(@NonNull String s) {
        this.s = s;
    }

    @IntRange(from = 0)
    public int getFoo() {
        return foo;
    }

    public void setFoo(int foo) {
        this.foo = foo;
    }


    public static AbsValidator<Demo> validator(Inspector inspector) {
        return new Validator_Demo(inspector);
    }
}
