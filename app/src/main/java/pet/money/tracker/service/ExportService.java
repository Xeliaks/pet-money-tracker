package pet.money.tracker.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pet.money.tracker.exception.AppException;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.util.DateUtils;
import pet.money.tracker.util.FormatUtils;

/** Exports transactions to JSON or CSV files. */
public class ExportService {

    private static final String[] CSV_HEADERS = {
        "id", "title", "amount", "category", "date", "description"
    };

    /**
     * Exports transactions to a pretty-printed JSON file.
     *
     * @param transactions the list to export
     * @param outputPath   destination file path
     * @throws AppException if the file cannot be written
     */
    public void exportToJson(List<Transaction> transactions, Path outputPath) throws AppException {
        try {
            Files.createDirectories(outputPath.getParent());
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
     * @throws AppException if the file cannot be written
     */
    public void exportToCsv(List<Transaction> transactions, Path outputPath) throws AppException {
        try {
            Files.createDirectories(outputPath.getParent());
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader(CSV_HEADERS)
                    .build();
            try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), format)) {
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