package pet.money.tracker.patterns;

/**
 * Observer that tracks whether the statistics cache is stale.
 *
 * <p>Registered with {@link pet.money.tracker.service.TransactionService}; any
 * mutation (add, update, delete) sets the dirty flag so downstream consumers
 * know to recompute statistics on the next access.
 */
public class StatisticsCacheObserver implements TransactionObserver {

    private boolean dirty;

    /** Creates an observer with a clean initial state. */
    public StatisticsCacheObserver() {
        this.dirty = false;
    }

    @Override
    public void onTransactionChanged(TransactionEvent event) {
        dirty = true;
    }

    /** @return {@code true} if a transaction mutation has occurred since the last {@link #markClean()} */
    public boolean isDirty() {
        return dirty;
    }

    /** Resets the dirty flag after statistics have been recomputed. */
    public void markClean() {
        dirty = false;
    }
}
