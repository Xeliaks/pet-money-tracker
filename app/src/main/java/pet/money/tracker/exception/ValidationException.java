package pet.money.tracker.exception;

/** Thrown when user-supplied input fails business-rule validation. */
public class ValidationException extends AppException {

    /** @param message description of the validation failure */
    public ValidationException(String message) {
        super(message);
    }
}