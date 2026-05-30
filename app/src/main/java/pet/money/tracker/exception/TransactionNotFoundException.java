package pet.money.tracker.exception;

/** Thrown when a transaction ID cannot be found in the store. */
public class TransactionNotFoundException extends AppException {

    /** @param id the missing transaction ID */
    public TransactionNotFoundException(String id) {
        super("Transaction not found: " + id);
    }
}
