package pet.money.tracker.exception;

/** Base runtime exception for the expense tracker application. */
public class AppException extends RuntimeException {

    /** @param message human-readable error description */
    public AppException(String message) {
        super(message);
    }

    /** @param message human-readable error description
     *  @param cause   underlying cause */
    public AppException(String message, Throwable cause) {
        super(message, cause);
    }
}