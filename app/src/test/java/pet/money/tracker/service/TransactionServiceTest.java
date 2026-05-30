package pet.money.tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pet.money.tracker.exception.TransactionNotFoundException;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Money;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.storage.StorageProvider;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    private static final String COFFEE = "Coffee";
    private static final String TX_ID = "id-1";
    private static final String ESPRESSO = "Espresso";

    @Mock
    private StorageProvider mockStorage;

    private TransactionService buildService(List<Transaction> initial) {
        when(mockStorage.loadAll()).thenReturn(new ArrayList<>(initial));
        return new TransactionService(mockStorage);
    }

    private Transaction tx(String id, String title, BigDecimal amount,
                           Category category, LocalDate date, String desc) {
        return new Transaction(id, title, Money.valueOf(amount), category, date, desc);
    }

    // ── Black-box ────────────────────────────────────────────────────────────

    @Test
    void add_returnsTransactionWithGeneratedId() {
        TransactionService service = buildService(List.of());
        Transaction result = service.add(COFFEE, new BigDecimal("3.50"), Category.FOOD,
                LocalDate.of(2024, 1, 15), "morning coffee");
        assertThat(result.getId()).isNotNull().isNotBlank();
    }

    @Test
    void add_twoTransactions_idsAreDistinct() {
        TransactionService service = buildService(List.of());
        Transaction t1 = service.add(COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.now(), null);
        Transaction t2 = service.add("Tea", BigDecimal.TEN, Category.FOOD, LocalDate.now(), null);
        assertThat(t1.getId()).isNotEqualTo(t2.getId());
    }

    @Test
    void add_callsSaveAll() {
        TransactionService service = buildService(List.of());
        service.add(COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.now(), null);
        verify(mockStorage).saveAll(anyList());
    }

    @Test
    void findById_unknownId_throwsTransactionNotFoundException() {
        TransactionService service = buildService(List.of());
        assertThatThrownBy(() -> service.findById("nonexistent"))
                .isInstanceOf(TransactionNotFoundException.class);
    }

    @Test
    void delete_removesTransactionFromList() {
        Transaction existing = tx(TX_ID, COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.now(), null);
        TransactionService service = buildService(List.of(existing));
        service.delete(TX_ID);
        assertThat(service.findAll()).isEmpty();
    }

    @Test
    void update_modifiesOnlyProvidedFields() {
        Transaction existing = tx(TX_ID, COFFEE, new BigDecimal("3.00"),
                Category.FOOD, LocalDate.of(2024, 1, 10), "old note");
        TransactionService service = buildService(List.of(existing));
        service.update(TX_ID, ESPRESSO, null, null, null, null);
        Transaction updated = service.findById(TX_ID);
        assertThat(updated.getTitle()).isEqualTo(ESPRESSO);
        assertThat(updated.getAmount().value()).isEqualByComparingTo(new BigDecimal("3.00"));
        assertThat(updated.getCategory()).isEqualTo(Category.FOOD);
        assertThat(updated.getDate()).isEqualTo(LocalDate.of(2024, 1, 10));
        assertThat(updated.getDescription()).isEqualTo("old note");
    }

    // ── White-box (branch coverage) ──────────────────────────────────────────

    @Test
    void search_nullKeyword_returnsAll() {
        TransactionService service = buildService(List.of(
                tx("1", COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.now(), null)));
        assertThat(service.search(null)).hasSize(1);
    }

    @Test
    void search_blankKeyword_returnsAll() {
        TransactionService service = buildService(List.of(
                tx("1", COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.now(), null)));
        assertThat(service.search("   ")).hasSize(1);
    }

    @Test
    void search_matchesTitleOnly() {
        TransactionService service = buildService(List.of(
                tx("1", COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.now(), "breakfast")));
        assertThat(service.search("coff")).hasSize(1);
    }

    @Test
    void search_matchesDescriptionOnly() {
        TransactionService service = buildService(List.of(
                tx("1", COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.now(), "morning brew")));
        assertThat(service.search("brew")).hasSize(1);
    }

    @Test
    void search_noMatch_returnsEmpty() {
        TransactionService service = buildService(List.of(
                tx("1", COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.now(), null)));
        assertThat(service.search("xyz")).isEmpty();
    }

    @Test
    void filterByDateRange_onBoundaryDates_included() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 1, 31);
        TransactionService service = buildService(List.of(
                tx("1", "Start", BigDecimal.ONE, Category.FOOD, from, null),
                tx("2", "End", BigDecimal.ONE, Category.FOOD, to, null)));
        assertThat(service.filterByDateRange(from, to)).hasSize(2);
    }

    @Test
    void sortByAmount_descending_largestFirst() {
        TransactionService service = buildService(List.of());
        List<Transaction> input = List.of(
                tx("1", "A", new BigDecimal("10.00"), Category.FOOD, LocalDate.now(), null),
                tx("2", "B", new BigDecimal("5.00"), Category.FOOD, LocalDate.now(), null),
                tx("3", "C", new BigDecimal("20.00"), Category.FOOD, LocalDate.now(), null));
        List<Transaction> sorted = service.sortByAmount(input, false);
        assertThat(sorted.get(0).getAmount().value()).isEqualByComparingTo(new BigDecimal("20.00"));
    }

    @Test
    void sortByDate_ascending_oldestFirst() {
        TransactionService service = buildService(List.of());
        List<Transaction> input = List.of(
                tx("1", "New", BigDecimal.ONE, Category.FOOD, LocalDate.of(2024, 3, 1), null),
                tx("2", "Old", BigDecimal.ONE, Category.FOOD, LocalDate.of(2024, 1, 1), null));
        List<Transaction> sorted = service.sortByDate(input, true);
        assertThat(sorted.get(0).getDate()).isEqualTo(LocalDate.of(2024, 1, 1));
    }

    @Test
    void filterByCategoryAndDateRange_categoryMismatch_excluded() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 12, 31);
        TransactionService service = buildService(List.of(
                tx("1", "Bus", BigDecimal.ONE, Category.TRANSPORT, LocalDate.of(2024, 6, 1), null)));
        assertThat(service.filterByCategoryAndDateRange(Category.FOOD, from, to)).isEmpty();
    }

    @Test
    void update_allFieldsProvided_updatesAll() {
        Transaction existing = tx(TX_ID, COFFEE, new BigDecimal("3.00"),
                Category.FOOD, LocalDate.of(2024, 1, 10), "old");
        TransactionService service = buildService(List.of(existing));
        service.update(TX_ID, ESPRESSO, new BigDecimal("4.50"),
                Category.OTHER, LocalDate.of(2024, 2, 20), "new note");
        Transaction updated = service.findById(TX_ID);
        assertThat(updated.getTitle()).isEqualTo(ESPRESSO);
        assertThat(updated.getAmount().value()).isEqualByComparingTo(new BigDecimal("4.50"));
        assertThat(updated.getCategory()).isEqualTo(Category.OTHER);
        assertThat(updated.getDate()).isEqualTo(LocalDate.of(2024, 2, 20));
        assertThat(updated.getDescription()).isEqualTo("new note");
    }

    @Test
    void sortByAmount_ascending_smallestFirst() {
        TransactionService service = buildService(List.of());
        List<Transaction> input = List.of(
                tx("1", "A", new BigDecimal("10.00"), Category.FOOD, LocalDate.now(), null),
                tx("2", "B", new BigDecimal("5.00"), Category.FOOD, LocalDate.now(), null));
        List<Transaction> sorted = service.sortByAmount(input, true);
        assertThat(sorted.get(0).getAmount().value()).isEqualByComparingTo(new BigDecimal("5.00"));
    }

    @Test
    void sortByDate_descending_newestFirst() {
        TransactionService service = buildService(List.of());
        List<Transaction> input = List.of(
                tx("1", "Old", BigDecimal.ONE, Category.FOOD, LocalDate.of(2024, 1, 1), null),
                tx("2", "New", BigDecimal.ONE, Category.FOOD, LocalDate.of(2024, 3, 1), null));
        List<Transaction> sorted = service.sortByDate(input, false);
        assertThat(sorted.get(0).getDate()).isEqualTo(LocalDate.of(2024, 3, 1));
    }

    @Test
    void filterByCategoryAndDateRange_outOfRange_excluded() {
        LocalDate from = LocalDate.of(2024, 1, 1);
        LocalDate to = LocalDate.of(2024, 6, 30);
        TransactionService service = buildService(List.of(
                tx("1", COFFEE, BigDecimal.ONE, Category.FOOD, LocalDate.of(2024, 8, 1), null)));
        assertThat(service.filterByCategoryAndDateRange(Category.FOOD, from, to)).isEmpty();
    }
}
