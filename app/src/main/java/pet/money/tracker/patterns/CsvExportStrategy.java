package pet.money.tracker.patterns;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.util.DateUtils;

/** Strategy that serialises transactions to a CSV file with a header row. */
public class CsvExportStrategy extends AbstractExportStrategy {

    private static final String[] CSV_HEADERS = {
        "id", "title", "amount", "category", "date", "description"
    };

    @Override
    protected void doExport(List<Transaction> transactions, Path outputPath) throws IOException {
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(CSV_HEADERS)
                .build();
        Writer writer = Files.newBufferedWriter(outputPath, StandardCharsets.UTF_8);
        try (CSVPrinter printer = new CSVPrinter(writer, format)) {
            for (Transaction t : transactions) {
                printer.printRecord(
                        t.getId(),
                        t.getTitle(),
                        t.getAmount().display(),
                        t.getCategory(),
                        DateUtils.format(t.getDate()),
                        t.getDescription() != null ? t.getDescription() : "");
            }
        }
    }

    @Override
    public String getFormatName() {
        return "csv";
    }
}
