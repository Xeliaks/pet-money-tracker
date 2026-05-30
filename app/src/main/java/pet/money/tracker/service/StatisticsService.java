package pet.money.tracker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;

/** Computes aggregate statistics over the transaction list. */
public class StatisticsService {

    private final TransactionService txService;

    /**
     * @param txService source of transaction data
     */
    public StatisticsService(TransactionService txService) {
        this.txService = txService;
    }

    /**
     * @return number of transactions per category
     */
    public Map<Category, Long> countByCategory() {
        return txService.findAll().stream()
                .collect(Collectors.groupingBy(Transaction::getCategory, Collectors.counting()));
    }

    /**
     * @return total amount spent per category
     */
    public Map<Category, BigDecimal> totalByCategory() {
        Map<Category, BigDecimal> result = new LinkedHashMap<>();
        for (Transaction t : txService.findAll()) {
            result.merge(t.getCategory(), t.getAmount(), BigDecimal::add);
        }
        return result;
    }

    /**
     * @param from start date (inclusive)
     * @param to   end date (inclusive)
     * @return sum of amounts for transactions within the period
     */
    public BigDecimal totalForPeriod(LocalDate from, LocalDate to) {
        return txService.filterByDateRange(from, to).stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * @param from start date (inclusive)
     * @param to   end date (inclusive)
     * @return average amount for the period, or zero if no transactions exist
     */
    public BigDecimal averageForPeriod(LocalDate from, LocalDate to) {
        List<Transaction> inPeriod = txService.filterByDateRange(from, to);
        if (inPeriod.isEmpty()) {
            return BigDecimal.ZERO;
        }
        BigDecimal total = inPeriod.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(inPeriod.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * @return sum of all transaction amounts
     */
    public BigDecimal grandTotal() {
        return txService.findAll().stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
