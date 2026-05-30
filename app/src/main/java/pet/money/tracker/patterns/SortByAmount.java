package pet.money.tracker.patterns;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import pet.money.tracker.model.Transaction;

/** Sort strategy that orders transactions by monetary amount. */
public class SortByAmount implements SortStrategy {

    private final boolean ascending;

    /**
     * @param ascending {@code true} for smallest-first, {@code false} for largest-first
     */
    public SortByAmount(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public List<Transaction> sort(List<Transaction> transactions) {
        Comparator<Transaction> cmp = Comparator.comparing(Transaction::getAmount);
        return transactions.stream()
                .sorted(ascending ? cmp : cmp.reversed())
                .collect(Collectors.toList());
    }
}
