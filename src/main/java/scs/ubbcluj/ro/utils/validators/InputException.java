package scs.ubbcluj.ro.utils.validators;

public class InputException extends IllegalArgumentException{
    public InputException() {
    }

    public InputException(String s) {
        super(s);
    }

    public InputException(String message, Throwable cause) {
        super(message, cause);
    }

    public InputException(Throwable cause) {
        super(cause);
    }
}
