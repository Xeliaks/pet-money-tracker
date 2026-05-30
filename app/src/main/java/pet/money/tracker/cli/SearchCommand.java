package pet.money.tracker.cli;

import java.util.List;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.util.FormatUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Searches transactions by keyword across title and description. */
@Command(name = "search", description = "Search transactions by keyword.", mixinStandardHelpOptions = true)
public class SearchCommand implements Runnable {

    @Option(names = {"--keyword"}, required = true, description = "Keyword to search for.")
    private String keyword;

    @Override
    public void run() {
        List<Transaction> results = MainCommand.getTxService().search(keyword);
        if (results.isEmpty()) {
            System.out.println("No transactions match '" + keyword + "'.");
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
