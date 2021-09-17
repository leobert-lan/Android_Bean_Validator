package osp.leobert.android.inspector.sample.bean;

import androidx.annotation.NonNull;
import androidx.annotation.StringDef;

import osp.leobert.android.inspector.notations.GenerateValidator;

/*
 * <p><b>Package:</b> osp.leobert.android.inspector.sample.bean </p>
 * <p><b>Project:</b> Jsr380 </p>
 * <p><b>Classname:</b> Bar </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/11/21.
 */
@GenerateValidator
public class Bar {

    String bar;

    @StringDef("bar")
    @interface BarDef{

    }

    @NonNull
    @BarDef
    public String getBar() {
        return bar;
    }
}
