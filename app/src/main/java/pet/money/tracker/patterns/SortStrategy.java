package pet.money.tracker.patterns;

import java.util.List;
import pet.money.tracker.model.Transaction;

/** Strategy interface for sorting a list of transactions. */
public interface SortStrategy {

    /**
     * Returns a new list containing the same transactions in sorted order.
     * The input list is not mutated.
     *
     * @param transactions the list to sort
     * @return sorted copy
     */
    List<Transaction> sort(List<Transaction> transactions);
}
