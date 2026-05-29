package pet.money.tracker.cli;

import java.nio.file.Path;
import java.util.List;
import pet.money.tracker.exception.ValidationException;
import pet.money.tracker.model.Transaction;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/** Exports all transactions to a JSON or CSV file. */
@Command(name = "export", description = "Export transactions to a file.", mixinStandardHelpOptions = true)
public class ExportCommand implements Runnable {

    @Option(names = {"--format"}, required = true,
            description = "Output format: json or csv.")
    private String format;

    @Option(names = {"--output"}, required = true,
            description = "Destination file path.")
    private String output;

    @Override
    public void run() {
        List<Transaction> all = MainCommand.getTxService().findAll();
        Path outputPath = Path.of(output);

        if ("json".equalsIgnoreCase(format)) {
            MainCommand.getExportService().exportToJson(all, outputPath);
        } else if ("csv".equalsIgnoreCase(format)) {
            MainCommand.getExportService().exportToCsv(all, outputPath);
        } else {
            throw new ValidationException("Unknown format '" + format + "'. Use 'json' or 'csv'.");
        }

        System.out.printf("Exported %d transaction(s) to %s%n", all.size(), outputPath);
    }
}