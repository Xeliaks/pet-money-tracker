package pet.money.tracker.cli;

import java.math.BigDecimal;
import java.util.Map;
import pet.money.tracker.model.Category;
import pet.money.tracker.service.StatisticsService;
import pet.money.tracker.util.FormatUtils;
import picocli.CommandLine.Command;

/** Displays aggregate statistics across all transactions. */
@Command(name = "stats", description = "Show transaction statistics.", mixinStandardHelpOptions = true)
public class StatsCommand implements Runnable {

    @Override
    public void run() {
        StatisticsService stats = MainCommand.getStatsService();

        System.out.println("=== Transaction Statistics ===");
        System.out.printf("Grand total : %s%n", FormatUtils.formatAmount(stats.grandTotal()));
        System.out.println();

        System.out.println("--- By Category ---");
        Map<Category, Long>       counts = stats.countByCategory();
        Map<Category, BigDecimal> totals = stats.totalByCategory();

        for (Category cat : Category.values()) {
            long count = counts.getOrDefault(cat, 0L);
            if (count > 0) {
                BigDecimal total = totals.getOrDefault(cat, BigDecimal.ZERO);
                System.out.printf("  %-14s  %3d transaction(s)  total: %s%n",
                        cat, count, FormatUtils.formatAmount(total));
            }
        }
    }
}