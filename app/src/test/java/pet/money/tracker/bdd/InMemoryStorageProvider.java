package pet.money.tracker.bdd;

import java.util.ArrayList;
import java.util.List;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.storage.StorageProvider;

/** ArrayList-backed StorageProvider used exclusively in BDD step definitions. */
class InMemoryStorageProvider implements StorageProvider {

    private final List<Transaction> store = new ArrayList<>();

    @Override
    public List<Transaction> loadAll() {
        return new ArrayList<>(store);
    }

    @Override
    public void saveAll(List<Transaction> transactions) {
        store.clear();
        store.addAll(transactions);
    }
}