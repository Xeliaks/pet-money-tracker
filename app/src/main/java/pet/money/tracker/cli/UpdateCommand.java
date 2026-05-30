package pet.money.tracker.cli;

import java.math.BigDecimal;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.util.DateUtils;
import pet.money.tracker.util.FormatUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Updates one or more fields of an existing transaction. */
@Command(name = "update", description = "Update a transaction by ID.", mixinStandardHelpOptions = true)
public class UpdateCommand implements Runnable {

    @Option(names = {"--id"}, required = true, description = "ID of the transaction to update.")
    private String id;

    @Option(names = {"-t", "--title"}, description = "New title.")
    private String title;

    @Option(names = {"-a", "--amount"}, description = "New amount.")
    private BigDecimal amount;

    @Option(names = {"-c", "--category"}, description = "New category: ${COMPLETION-CANDIDATES}.")
    private Category category;

    @Option(names = {"-d", "--date"}, description = "New date (yyyy-MM-dd).")
    private String date;

    @Option(names = {"--desc"}, description = "New description.")
    private String description;

    @Override
    public void run() {
        java.time.LocalDate parsedDate = (date != null) ? DateUtils.parse(date) : null;
        Transaction updated = MainCommand.getTxService()
                .update(id, title, amount, category, parsedDate, description);
        System.out.println("Updated transaction " + updated.getId());
        System.out.println(FormatUtils.tableHeader());
        System.out.println(FormatUtils.tableSeparator());
        System.out.println(FormatUtils.formatTransaction(updated));
    }
}
