package pet.money.tracker.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a single financial transaction.
 *
 * <p>Uses a standard class (not a record) to support Jackson's no-arg
 * constructor requirement without extra configuration.
 */
public class Transaction {

    private String id;
    private String title;
    private Money amount;
    private Category category;
    private LocalDate date;
    private String description;

    /** No-arg constructor required by Jackson. */
    public Transaction() {
    }

    /**
     * Full constructor.
     *
     * @param id          unique identifier (UUID string)
     * @param title       short label for the transaction
     * @param amount      monetary amount (positive for expense, may be positive for income)
     * @param category    spending category
     * @param date        transaction date
     * @param description optional free-text notes
     */
    public Transaction(String id, String title, Money amount,
                       Category category, LocalDate date, String description) {
        this.id = id;
        this.title = title;
        this.amount = amount;
        this.category = category;
        this.date = date;
        this.description = description;
    }

    /** @return unique transaction ID */
    public String getId() {
        return id;
    }

    /** @param id unique transaction ID */
    public void setId(String id) {
        this.id = id;
    }

    /** @return transaction title */
    public String getTitle() {
        return title;
    }

    /** @param title transaction title */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @return monetary amount */
    public Money getAmount() {
        return amount;
    }

    /** @param amount monetary amount */
    public void setAmount(Money amount) {
        this.amount = amount;
    }

    /** @return spending category */
    public Category getCategory() {
        return category;
    }

    /** @param category spending category */
    public void setCategory(Category category) {
        this.category = category;
    }

    /** @return transaction date */
    public LocalDate getDate() {
        return date;
    }

    /** @param date transaction date */
    public void setDate(LocalDate date) {
        this.date = date;
    }

    /** @return optional free-text description */
    public String getDescription() {
        return description;
    }

    /** @param description optional free-text description */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Transaction)) {
            return false;
        }
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Transaction{"
                + "id='" + id + '\''
                + ", title='" + title + '\''
                + ", amount=" + amount
                + ", category=" + category
                + ", date=" + date
                + '}';
    }
}
