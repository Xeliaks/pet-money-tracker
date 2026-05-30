package pet.money.tracker.storage;

import java.util.List;
import pet.money.tracker.exception.StorageException;
import pet.money.tracker.model.Transaction;

/** Contract for loading and persisting the transaction list. */
public interface StorageProvider {

    /**
     * Loads all transactions from the backing store.
     *
     * @return mutable list of transactions (empty if the store has no data yet)
     * @throws StorageException if the data cannot be read
     */
    List<Transaction> loadAll();

    /**
     * Persists the complete list of transactions, replacing any existing data.
     *
     * @param transactions the authoritative list to save
     * @throws StorageException if the data cannot be written
     */
    void saveAll(List<Transaction> transactions);
}
