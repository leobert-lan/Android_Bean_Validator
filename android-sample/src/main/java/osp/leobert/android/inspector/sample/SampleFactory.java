package osp.leobert.android.inspector.sample;

import com.google.auto.value.AutoValue;

import osp.leobert.android.inspector.notations.GenerateValidator;
import osp.leobert.android.inspector.notations.InspectorFactory;
import osp.leobert.android.inspector.validators.Validator;


@InspectorFactory(include = {AutoValue.class,GenerateValidator.class})
public abstract class SampleFactory
        implements Validator.Factory {

    public static SampleFactory create() {
        return new InspectorFactory_SampleFactory();
    }
}
