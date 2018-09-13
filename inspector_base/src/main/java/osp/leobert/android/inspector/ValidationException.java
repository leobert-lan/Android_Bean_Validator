package osp.leobert.android.inspector;

/**
 * <p><b>Package:</b> osp.leobert.android.inspector </p>
 * <p><b>Project:</b> Jsr380 </p>
 * <p><b>Classname:</b> ValidationException </p>
 * <p><b>Description:</b> TODO </p>
 * Created by leobert on 2018/9/12.
 */
public class ValidationException extends RuntimeException {
    public ValidationException() {
    }

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }

    public ValidationException(Throwable cause) {
        super(cause);
    }

    public ValidationException(String message,
                               Throwable cause,
                               boolean enableSuppression,
                               boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
