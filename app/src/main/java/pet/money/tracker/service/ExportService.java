package pet.money.tracker.service;

import java.nio.file.Path;
import java.util.List;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.patterns.CsvExportStrategy;
import pet.money.tracker.patterns.ExportContext;
import pet.money.tracker.patterns.JsonExportStrategy;

/** Exports transactions to JSON or CSV files by delegating to an {@link ExportContext}. */
public class ExportService {

    /**
     * Exports transactions to a pretty-printed JSON file.
     *
     * @param transactions the list to export
     * @param outputPath   destination file path
     */
    public void exportToJson(List<Transaction> transactions, Path outputPath) {
        new ExportContext(new JsonExportStrategy()).execute(transactions, outputPath);
    }

    /**
     * Exports transactions to a CSV file with a header row.
     *
     * @param transactions the list to export
     * @param outputPath   destination file path
     */
    public void exportToCsv(List<Transaction> transactions, Path outputPath) {
        new ExportContext(new CsvExportStrategy()).execute(transactions, outputPath);
    }
}
