package osp.leobert.android.inspector.sample.bean;

import java.util.Date;

import osp.leobert.android.inspector.ValidationException;
import osp.leobert.android.inspector.validators.Validator;

/**
 * <p><b>Package:</b> android.test </p>
 * <p><b>Project:</b> MotorFans </p>
 * <p><b>Classname:</b> BarIValidator </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2019/1/26.
 */
public final class BarIValidator extends Validator<Date> {

    @Override
    public void validate(Date date) throws ValidationException {
        if (date == null) throw new ValidationException("date must be nonnull");
    }
}
