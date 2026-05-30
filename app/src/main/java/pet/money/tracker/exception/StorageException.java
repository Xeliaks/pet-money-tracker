package pet.money.tracker.exception;

/** Thrown when a read or write operation to persistent storage fails. */
public class StorageException extends AppException {

    /** @param message description of the I/O failure */
    public StorageException(String message) {
        super(message);
    }

    /** @param message description of the I/O failure
     *  @param cause   underlying I/O exception */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
