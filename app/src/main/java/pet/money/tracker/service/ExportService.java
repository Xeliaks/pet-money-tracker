package pet.money.tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            ObjectMapper mapper = new ObjectMapper()
                    .registerModule(new JavaTimeModule())
                    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                    .enable(SerializationFeature.INDENT_OUTPUT);
            mapper.writeValue(outputPath.toFile(), transactions);
        } catch (IOException e) {
            throw new AppException("Failed to export JSON to " + outputPath, e);
        }
    }

    /**
     * Exports transactions to a CSV file with a header row.
     *
     * @param transactions the list to export
     * @param outputPath   destination file path
     */
    public void exportToCsv(List<Transaction> transactions, Path outputPath) {
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader(CSV_HEADERS)
                    .build();
            Writer writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);
            try (CSVPrinter printer = new CSVPrinter(writer, format)) {
                for (Transaction t : transactions) {
                    printer.printRecord(
                            t.getId(),
                            t.getTitle(),
                            FormatUtils.formatAmount(t.getAmount()),
                            t.getCategory(),
                            DateUtils.format(t.getDate()),
                            t.getDescription() != null ? t.getDescription() : "");
                }
            }
        } catch (IOException e) {
            throw new AppException("Failed to export CSV to " + outputPath, e);
        }
    }
}
