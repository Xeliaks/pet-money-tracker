package pet.money.tracker.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import pet.money.tracker.patterns.StorageProviderFactory;
import pet.money.tracker.service.ExportService;
import pet.money.tracker.service.StatisticsService;
import pet.money.tracker.service.TransactionService;
import pet.money.tracker.storage.StorageProvider;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Entry point and root command for the Personal Expense Tracker CLI.
 *
 * <p>Bootstraps all services and registers subcommands. Services are held as
 * static fields so every subcommand can access them without dependency injection
 * framework overhead.
 */
@Command(
        name = "expense-tracker",
        mixinStandardHelpOptions = true,
        version = "1.0.0",
        description = "Personal Expense Tracker — manage and report your transactions.",
        subcommands = {
            AddCommand.class,
            ListCommand.class,
            UpdateCommand.class,
            DeleteCommand.class,
            SearchCommand.class,
            FilterCommand.class,
            ExportCommand.class,
            StatsCommand.class,
            CommandLine.HelpCommand.class
        }
)
public class MainCommand implements Runnable {

    private static TransactionService txService;
    private static StatisticsService statsService;
    private static ExportService exportService;

    static {
        Path dataDir = Path.of(System.getProperty("user.home"), ".pet-tracker");
        try {
            Files.createDirectories(dataDir);
        } catch (IOException e) {
            System.err.println("Warning: could not create data directory: " + e.getMessage());
        }
        StorageProvider storage = StorageProviderFactory.forFormat("json")
                .createProvider(dataDir.resolve("transactions.json"));
        txService = new TransactionService(storage);
        statsService = new StatisticsService(txService);
        exportService = new ExportService();
    }

    /** @return the shared TransactionService instance */
    public static TransactionService getTxService() {
        return txService;
    }

    /** @return the shared StatisticsService instance */
    public static StatisticsService getStatsService() {
        return statsService;
    }

    /** @return the shared ExportService instance */
    public static ExportService getExportService() {
        return exportService;
    }

    @Override
    public void run() {
        CommandLine.usage(this, System.out);
    }

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        int exitCode = new CommandLine(new MainCommand()).execute(args);
        System.exit(exitCode);
    }
}
