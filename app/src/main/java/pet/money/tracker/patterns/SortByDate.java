package pet.money.tracker.patterns;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import pet.money.tracker.model.Transaction;

/** Sort strategy that orders transactions by date. */
public class SortByDate implements SortStrategy {

    private final boolean ascending;

    /**
     * @param ascending {@code true} for oldest-first, {@code false} for newest-first
     */
    public SortByDate(boolean ascending) {
        this.ascending = ascending;
    }

    @Override
    public List<Transaction> sort(List<Transaction> transactions) {
        Comparator<Transaction> cmp = Comparator.comparing(Transaction::getDate);
        return transactions.stream()
                .sorted(ascending ? cmp : cmp.reversed())
                .collect(Collectors.toList());
    }
}
