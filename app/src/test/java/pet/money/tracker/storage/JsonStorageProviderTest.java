package pet.money.tracker.storage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import pet.money.tracker.exception.StorageException;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Money;
import pet.money.tracker.model.Transaction;

class JsonStorageProviderTest {

    @TempDir
    Path tempDir;

    @Test
    void loadAll_missingFile_returnsEmptyList() {
        Path missing = tempDir.resolve("nonexistent.json");
        JsonStorageProvider provider = new JsonStorageProvider(missing);
        assertThat(provider.loadAll()).isEmpty();
    }

    @Test
    void saveAndLoad_roundTripsAllFields() throws Exception {
        Path file = tempDir.resolve("data.json");
        JsonStorageProvider provider = new JsonStorageProvider(file);

        Transaction original = new Transaction(
                "id-1", "Groceries", Money.valueOf(new BigDecimal("45.00")),
                Category.FOOD, LocalDate.of(2024, 6, 15), "weekly shop");
        provider.saveAll(List.of(original));

        List<Transaction> loaded = provider.loadAll();
        assertThat(loaded).hasSize(1);
        Transaction t = loaded.get(0);
        assertThat(t.getId()).isEqualTo("id-1");
        assertThat(t.getTitle()).isEqualTo("Groceries");
        assertThat(t.getAmount().value()).isEqualByComparingTo(new BigDecimal("45.00"));
        assertThat(t.getCategory()).isEqualTo(Category.FOOD);
        assertThat(t.getDate()).isEqualTo(LocalDate.of(2024, 6, 15));
        assertThat(t.getDescription()).isEqualTo("weekly shop");
    }

    @Test
    void saveAll_createsParentDirectories() {
        Path nested = tempDir.resolve("sub/dir/data.json");
        JsonStorageProvider provider = new JsonStorageProvider(nested);
        provider.saveAll(List.of());
        assertThat(Files.exists(nested)).isTrue();
    }

    @Test
    void loadAll_corruptedFile_throwsStorageException() throws Exception {
        Path file = tempDir.resolve("corrupt.json");
        Files.writeString(file, "{ not valid json [[[");
        JsonStorageProvider provider = new JsonStorageProvider(file);
        assertThatThrownBy(provider::loadAll)
                .isInstanceOf(StorageException.class);
    }
}
