package pet.money.tracker.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;

class ExportServiceTest {

    @TempDir
    Path tempDir;

    private final ExportService exportService = new ExportService();

    private Transaction tx(String id, String title, BigDecimal amount,
                           Category category, LocalDate date) {
        return new Transaction(id, title, amount, category, date, "note");
    }

    @Test
    void exportToCsv_producesHeaderRow() throws Exception {
        Path out = tempDir.resolve("out.csv");
        exportService.exportToCsv(List.of(), out);
        List<String> lines = Files.readAllLines(out);
        assertThat(lines.get(0)).contains("id", "title", "amount", "category", "date");
    }

    @Test
    void exportToCsv_producesOneRowPerTransaction() throws Exception {
        Path out = tempDir.resolve("out.csv");
        List<Transaction> transactions = List.of(
                tx("1", "Coffee", new BigDecimal("3.50"), Category.FOOD, LocalDate.of(2024, 1, 1)),
                tx("2", "Bus", new BigDecimal("2.00"), Category.TRANSPORT, LocalDate.of(2024, 1, 2)));
        exportService.exportToCsv(transactions, out);
        List<String> lines = Files.readAllLines(out);
        assertThat(lines).hasSize(3); // header + 2 data rows
    }

    @Test
    void exportToJson_producesValidJson() throws Exception {
        Path out = tempDir.resolve("out.json");
        exportService.exportToJson(List.of(
                tx("1", "Coffee", new BigDecimal("3.50"), Category.FOOD, LocalDate.of(2024, 1, 1))), out);
        String content = Files.readString(out);
        assertThat(content).startsWith("[");
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        List<Transaction> parsed = mapper.readValue(out.toFile(), new TypeReference<>() {});
        assertThat(parsed).hasSize(1);
    }

    @Test
    void exportToJson_roundTripsLocalDate() throws Exception {
        Path out = tempDir.resolve("out.json");
        LocalDate date = LocalDate.of(2024, 6, 15);
        exportService.exportToJson(List.of(
                tx("1", "Coffee", new BigDecimal("3.50"), Category.FOOD, date)), out);
        ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
        List<Transaction> parsed = mapper.readValue(out.toFile(), new TypeReference<>() {});
        assertThat(parsed.get(0).getDate()).isEqualTo(date);
    }
}
