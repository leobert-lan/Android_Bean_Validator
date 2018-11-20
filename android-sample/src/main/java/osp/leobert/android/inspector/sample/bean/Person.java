package osp.leobert.android.inspector.sample.bean;

import android.support.annotation.IntDef;
import android.support.annotation.IntRange;
import android.support.annotation.LongDef;
import android.support.annotation.Nullable;
import android.support.annotation.Size;
import android.support.annotation.StringDef;

import com.google.auto.value.AutoValue;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.notations.InspectorIgnored;
import osp.leobert.android.inspector.validators.Validator;


@AutoValue
public abstract class Person<T, V> {

    public static final String FOO = "foo";
    public static final String FOO2 = "foo2";
    public static final int FOO_INT = 0;
    public static final long FOO_LONG = 0L;

    @StringDef({FOO, FOO2})
    public @interface StringDefChecked {
    }

    @IntDef(FOO_INT)
    public @interface IntDefChecked {
    }

    @LongDef(FOO_LONG)
    public @interface LongDefChecked {
    }

    public abstract String firstName();

    public abstract String lastName();

    @SuppressWarnings("mutable")
    public abstract int[] favoriteNumbers();

    public abstract List<String> aList();

    public abstract Map<String, String> aMap();

    public abstract Set<String> favoriteFoods();

    @StringDefChecked
    public abstract String stringDefChecked();

    @IntDefChecked
    public abstract int intDefChecked();

    @LongDefChecked
    public abstract long longDefChecked();

    @IntRange(from = 0)
    public abstract int age();

    @Nullable
    public abstract String occupation();


    @InspectorIgnored
    public abstract String uuid();

    @Size(multiple = 2)
    public abstract List<String> doublesOfStrings();

    @Size(3)
    public abstract Map<String, String> threePairs();

    @Size(min = 3)
    public abstract Set<String> atLeastThreeStrings();

    @Size(max = 3)
    public abstract Set<String> atMostThreeStrings();


    public abstract T genericOne();

    public abstract V genericTwo();

    public static <T, V> Validator<Person<T, V>> validator(Inspector inspector, Type[] types) {
        return new Validator_Person<>(inspector, types);
    }
}
