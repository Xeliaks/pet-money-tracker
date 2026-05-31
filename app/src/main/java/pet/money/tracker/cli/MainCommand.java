package pet.money.tracker.cli;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import pet.money.tracker.patterns.StorageProviderFactory;
import pet.money.tracker.service.ExportService;
import pet.money.tracker.service.StatisticsService;
import pet.money.tracker.service.TransactionService;
import pet.money.tracker.storage.StorageProvider;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Spec;

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

    @Spec
    private CommandLine.Model.CommandSpec spec;

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
        CommandLine cmd = spec.commandLine();
        try (Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8)) {
            cmd.getOut().println("expense-tracker shell - type 'help' for commands, 'exit' to quit.");
            String line = readNextLine(scanner, cmd);
            while (line != null && !isExitCommand(line)) {
                if (!line.isEmpty()) {
                    cmd.execute(tokenize(line));
                }
                line = readNextLine(scanner, cmd);
            }
        }
    }

    private static String readNextLine(Scanner scanner, CommandLine cmd) {
        if (!scanner.hasNextLine()) {
            return null;
        }
        cmd.getOut().print("> ");
        cmd.getOut().flush();
        return scanner.nextLine().trim();
    }

    private static boolean isExitCommand(String line) {
        return "exit".equalsIgnoreCase(line) || "quit".equalsIgnoreCase(line);
    }

    private static String[] tokenize(String line) {
        List<String> tokens = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        char quoteChar = 0;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == quoteChar) {
                    inQuotes = false;
                } else {
                    current.append(c);
                }
            } else if (c == '"' || c == '\'') {
                inQuotes = true;
                quoteChar = c;
            } else if (c == ' ' || c == '\t') {
                flushToken(current, tokens);
            } else {
                current.append(c);
            }
        }
        flushToken(current, tokens);
        return tokens.toArray(String[]::new);
    }

    private static void flushToken(StringBuilder current, List<String> tokens) {
        if (!current.isEmpty()) {
            tokens.add(current.toString());
            current.setLength(0);
        }
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
