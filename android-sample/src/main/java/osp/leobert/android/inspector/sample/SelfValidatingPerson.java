package osp.leobert.android.inspector.sample;

import com.google.auto.value.AutoValue;

import osp.leobert.android.inspector.Inspector;
import osp.leobert.android.inspector.SelfValidating;
import osp.leobert.android.inspector.ValidationException;


@AutoValue
public abstract class SelfValidatingPerson implements SelfValidating {

    @Override
    public final void validate(Inspector inspector) throws ValidationException {
        // Great!
    }
}
