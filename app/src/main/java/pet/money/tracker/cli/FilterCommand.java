package pet.money.tracker.cli;

import java.time.LocalDate;
import java.util.List;
import pet.money.tracker.exception.ValidationException;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.util.DateUtils;
import pet.money.tracker.util.FormatUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Filters transactions by category and/or date range. */
@Command(name = "filter", description = "Filter transactions by category and/or date range.",
        mixinStandardHelpOptions = true)
public class FilterCommand implements Runnable {

    @Option(names = {"--category"}, description = "Filter by category: ${COMPLETION-CANDIDATES}.")
    private Category category;

    @Option(names = {"--from"}, description = "Start date inclusive (yyyy-MM-dd).")
    private String from;

    @Option(names = {"--to"}, description = "End date inclusive (yyyy-MM-dd).")
    private String to;

    @Override
    public void run() {
        if (category == null && from == null && to == null) {
            throw new ValidationException("Specify at least one filter: --category, --from, --to.");
        }

        LocalDate fromDate = (from != null) ? DateUtils.parse(from) : LocalDate.MIN;
        LocalDate toDate   = (to   != null) ? DateUtils.parse(to)   : LocalDate.MAX;

        List<Transaction> results;
        if (category != null) {
            results = MainCommand.getTxService()
                    .filterByCategoryAndDateRange(category, fromDate, toDate);
        } else {
            results = MainCommand.getTxService().filterByDateRange(fromDate, toDate);
        }

        if (results.isEmpty()) {
            System.out.println("No transactions match the given filters.");
            return;
        }
        System.out.println(FormatUtils.tableHeader());
        System.out.println(FormatUtils.tableSeparator());
        for (Transaction t : results) {
            System.out.println(FormatUtils.formatTransaction(t));
        }
        System.out.printf("Found: %d transaction(s)%n", results.size());
    }
}
