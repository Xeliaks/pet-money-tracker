package pet.money.tracker.cli;

import java.util.List;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.patterns.SortByAmount;
import pet.money.tracker.patterns.SortByDate;
import pet.money.tracker.patterns.SortStrategy;
import pet.money.tracker.util.FormatUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Lists all transactions with optional sorting. */
@Command(name = "list", description = "List all transactions.", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {

    @Option(names = {"--sort-by"},
            description = "Sort field: date or amount (default: date).",
            defaultValue = "date")
    private String sortBy;

    @Option(names = {"--order"},
            description = "Sort order: asc or desc (default: asc).",
            defaultValue = "asc")
    private String order;

    @Override
    public void run() {
        List<Transaction> all = MainCommand.getTxService().findAll();
        boolean ascending = !"desc".equalsIgnoreCase(order);

        SortStrategy strategy = "amount".equalsIgnoreCase(sortBy)
                ? new SortByAmount(ascending)
                : new SortByDate(ascending);
        List<Transaction> sorted = strategy.sort(all);

        if (sorted.isEmpty()) {
            System.out.println("No transactions found.");
            return;
        }

        System.out.println(FormatUtils.tableHeader());
        System.out.println(FormatUtils.tableSeparator());
        for (Transaction t : sorted) {
            System.out.println(FormatUtils.formatTransaction(t));
        }
        System.out.println(FormatUtils.tableSeparator());
        System.out.printf("Total: %d transaction(s)%n", sorted.size());
    }
}
