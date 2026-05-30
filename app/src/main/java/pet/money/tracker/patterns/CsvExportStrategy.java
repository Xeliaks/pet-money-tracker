package pet.money.tracker.patterns;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pet.money.tracker.exception.AppException;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.util.DateUtils;
import pet.money.tracker.util.FormatUtils;

/** Strategy that serialises transactions to a CSV file with a header row. */
public class CsvExportStrategy implements ExportStrategy {

    private static final String[] CSV_HEADERS = {
        "id", "title", "amount", "category", "date", "description"
    };

    @Override
    public void export(List<Transaction> transactions, Path outputPath) {
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

    @Override
    public String getFormatName() {
        return "csv";
    }
}
