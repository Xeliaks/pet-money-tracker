package pet.money.tracker.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.patterns.StatisticsCacheObserver;

/** Computes aggregate statistics over the transaction list. */
public class StatisticsService {

    private final TransactionService txService;
    private final StatisticsCacheObserver cacheObserver;

    private BigDecimal cachedGrandTotal;
    private Map<Category, BigDecimal> cachedTotalByCategory;
    private Map<Category, Long> cachedCountByCategory;

    /**
     * @param txService source of transaction data
     */
    public StatisticsService(TransactionService txService) {
        this.txService = txService;
        this.cacheObserver = new StatisticsCacheObserver();
        txService.addObserver(cacheObserver);
    }

    /**
     * @return number of transactions per category
     */
    public Map<Category, Long> countByCategory() {
        if (cachedCountByCategory == null || cacheObserver.isDirty()) {
            refreshCaches();
        }
        return Map.copyOf(cachedCountByCategory);
    }

    /**
     * @return total amount spent per category
     */
    public Map<Category, BigDecimal> totalByCategory() {
        if (cachedTotalByCategory == null || cacheObserver.isDirty()) {
            refreshCaches();
        }
        return Map.copyOf(cachedTotalByCategory);
    }

    /**
     * @param from start date (inclusive)
     * @param to   end date (inclusive)
     * @return sum of amounts for transactions within the period
     */
    public BigDecimal totalForPeriod(LocalDate from, LocalDate to) {
        return txService.filterByDateRange(from, to).stream()
                .map(t -> t.getAmount().value())
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
                .map(t -> t.getAmount().value())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return total.divide(BigDecimal.valueOf(inPeriod.size()), 2, RoundingMode.HALF_UP);
    }

    /**
     * @return sum of all transaction amounts
     */
    public BigDecimal grandTotal() {
        if (cachedGrandTotal == null || cacheObserver.isDirty()) {
            refreshCaches();
        }
        return cachedGrandTotal;
    }

    private void refreshCaches() {
        List<Transaction> all = txService.findAll();
        Map<Category, Long> counts = new LinkedHashMap<>();
        Map<Category, BigDecimal> totals = new LinkedHashMap<>();
        BigDecimal total = BigDecimal.ZERO;
        for (Transaction t : all) {
            counts.merge(t.getCategory(), 1L, Long::sum);
            totals.merge(t.getCategory(), t.getAmount().value(), BigDecimal::add);
            total = total.add(t.getAmount().value());
        }
        cachedCountByCategory = counts;
        cachedTotalByCategory = totals;
        cachedGrandTotal = total;
        cacheObserver.markClean();
    }
}
