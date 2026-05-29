package pet.money.tracker.patterns;

/** Observer notified of any add, update, or delete on the transaction list. */
public interface TransactionObserver {

    /**
     * Called after a transaction mutation has been persisted.
     *
     * @param event describes the mutation that occurred
     */
    void onTransactionChanged(TransactionEvent event);
}