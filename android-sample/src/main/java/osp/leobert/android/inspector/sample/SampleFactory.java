package osp.leobert.android.inspector.sample;

import com.google.auto.value.AutoValue;

import osp.leobert.android.inspector.notations.GenerateValidator;
import osp.leobert.android.inspector.notations.InspectorFactory;
import osp.leobert.android.inspector.validators.AbsValidator;


@InspectorFactory(include = {AutoValue.class})
public abstract class SampleFactory
        implements AbsValidator.Factory {

    public static SampleFactory create() {
        return new InspectorFactory_SampleFactory();
    }
}
