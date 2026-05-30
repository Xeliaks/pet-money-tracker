package pet.money.tracker.cli;

import java.math.BigDecimal;
import java.time.LocalDate;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.util.DateUtils;
import pet.money.tracker.util.FormatUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Adds a new expense or income transaction. */
@Command(name = "add", description = "Add a new transaction.", mixinStandardHelpOptions = true)
public class AddCommand implements Runnable {

    @Option(names = {"-t", "--title"}, required = true, description = "Transaction title.")
    private String title;

    @Option(names = {"-a", "--amount"}, required = true, description = "Amount (e.g. 12.50).")
    private BigDecimal amount;

    @Option(names = {"-c", "--category"}, required = true,
            description = "Category: ${COMPLETION-CANDIDATES}.")
    private Category category;

    @Option(names = {"-d", "--date"}, description = "Date in yyyy-MM-dd format (default: today).")
    private String date;

    @Option(names = {"--desc"}, description = "Optional description / notes.")
    private String description = "";

    @Override
    public void run() {
        LocalDate txDate = (date != null) ? DateUtils.parse(date) : DateUtils.today();
        Transaction t = MainCommand.getTxService()
                .add(title, amount, category, txDate, description);
        System.out.println("Added transaction " + t.getId());
        System.out.println(FormatUtils.tableHeader());
        System.out.println(FormatUtils.tableSeparator());
        System.out.println(FormatUtils.formatTransaction(t));
    }
}
