package pet.money.tracker.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.stream.Collectors;
import pet.money.tracker.exception.TransactionNotFoundException;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.patterns.SortByAmount;
import pet.money.tracker.patterns.SortByDate;
import pet.money.tracker.patterns.TransactionEvent;
import pet.money.tracker.patterns.TransactionObserver;
import pet.money.tracker.storage.StorageProvider;

/** Core business logic for managing transactions. */
public final class TransactionService {

    private final StorageProvider storage;
    private final List<Transaction> cache;
    private final List<TransactionObserver> observers = new ArrayList<>();

    /**
     * @param storage the persistence provider (JSON file or in-memory for tests)
     */
    public TransactionService(StorageProvider storage) {
        this.storage = storage;
        this.cache = new ArrayList<>(storage.loadAll());
    }

    /**
     * Registers an observer to be notified after every add, update, or delete.
     *
     * @param observer the observer to register
     */
    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    /**
     * Adds a new transaction and persists it.
     *
     * @param title       short label
     * @param amount      monetary amount
     * @param category    expense category
     * @param date        transaction date
     * @param description optional notes
     * @return the created transaction with a generated ID
     */
    public Transaction add(String title, BigDecimal amount, Category category,
                           LocalDate date, String description) {
        Transaction t = new Transaction(
                UUID.randomUUID().toString(), title, amount, category, date, description);
        cache.add(t);
        persist();
        notifyObservers(new TransactionEvent(TransactionEvent.EventType.ADDED, t));
        return t;
    }

    /**
     * Finds a transaction by its ID.
     *
     * @param id the unique identifier
     * @return the matching transaction
     * @throws TransactionNotFoundException if no transaction has that ID
     */
    public Transaction findById(String id) {
        return cache.stream()
                .filter(t -> t.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new TransactionNotFoundException(id));
    }

    /**
     * @return an unmodifiable snapshot of all transactions
     */
    public List<Transaction> findAll() {
        return List.copyOf(cache);
    }

    /**
     * Updates an existing transaction's fields and persists the change.
     *
     * @param id          ID of the transaction to update
     * @param title       new title (null to keep existing)
     * @param amount      new amount (null to keep existing)
     * @param category    new category (null to keep existing)
     * @param date        new date (null to keep existing)
     * @param description new description (null to keep existing)
     * @return the updated transaction
     * @throws TransactionNotFoundException if no transaction has that ID
     */
    public Transaction update(String id, String title, BigDecimal amount,
                              Category category, LocalDate date, String description) {
        Transaction t = findById(id);
        if (title != null) {
            t.setTitle(title);
        }
        if (amount != null) {
            t.setAmount(amount);
        }
        if (category != null) {
            t.setCategory(category);
        }
        if (date != null) {
            t.setDate(date);
        }
        if (description != null) {
            t.setDescription(description);
        }
        persist();
        notifyObservers(new TransactionEvent(TransactionEvent.EventType.UPDATED, t));
        return t;
    }

    /**
     * Deletes a transaction by ID and persists the change.
     *
     * @param id the unique identifier
     * @throws TransactionNotFoundException if no transaction has that ID
     */
    public void delete(String id) {
        Transaction t = findById(id);
        cache.remove(t);
        persist();
        notifyObservers(new TransactionEvent(TransactionEvent.EventType.DELETED, t));
    }

    /**
     * Searches transactions whose title or description contains the keyword,
     * case-insensitively.
     *
     * @param keyword the search term
     * @return matching transactions
     */
    public List<Transaction> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return findAll();
        }
        String lower = keyword.toLowerCase(Locale.ROOT);
        return cache.stream()
                .filter(t -> contains(t.getTitle(), lower) || contains(t.getDescription(), lower))
                .collect(Collectors.toList());
    }

    /**
     * @param category the category to filter by
     * @return transactions belonging to that category
     */
    public List<Transaction> filterByCategory(Category category) {
        return cache.stream()
                .filter(t -> t.getCategory() == category)
                .collect(Collectors.toList());
    }

    /**
     * @param from start date (inclusive)
     * @param to   end date (inclusive)
     * @return transactions within the date range
     */
    public List<Transaction> filterByDateRange(LocalDate from, LocalDate to) {
        return cache.stream()
                .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    /**
     * @param category the category to filter by
     * @param from     start date (inclusive)
     * @param to       end date (inclusive)
     * @return transactions matching both the category and date range
     */
    public List<Transaction> filterByCategoryAndDateRange(
            Category category, LocalDate from, LocalDate to) {
        return cache.stream()
                .filter(t -> t.getCategory() == category)
                .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
                .collect(Collectors.toList());
    }

    /**
     * @param list      the list to sort (not mutated)
     * @param ascending true for oldest-first, false for newest-first
     * @return a new sorted list
     */
    public List<Transaction> sortByDate(List<Transaction> list, boolean ascending) {
        return new SortByDate(ascending).sort(list);
    }

    /**
     * @param list      the list to sort (not mutated)
     * @param ascending true for smallest-first, false for largest-first
     * @return a new sorted list
     */
    public List<Transaction> sortByAmount(List<Transaction> list, boolean ascending) {
        return new SortByAmount(ascending).sort(list);
    }

    private void persist() {
        storage.saveAll(cache);
    }

    private void notifyObservers(TransactionEvent event) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionChanged(event);
        }
    }

    private boolean contains(String field, String lowerKeyword) {
        return field != null && field.toLowerCase(Locale.ROOT).contains(lowerKeyword);
    }
}
