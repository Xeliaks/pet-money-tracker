package pet.money.tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;
import pet.money.tracker.storage.StorageProvider;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private StorageProvider mockStorage;

    private StatisticsService buildStatsService(List<Transaction> transactions) {
        when(mockStorage.loadAll()).thenReturn(new ArrayList<>(transactions));
        TransactionService txService = new TransactionService(mockStorage);
        return new StatisticsService(txService);
    }

    private Transaction tx(String id, BigDecimal amount, Category category, LocalDate date) {
        return new Transaction(id, "title", amount, category, date, null);
    }

    @Test
    void countByCategory_groupsCorrectly() {
        StatisticsService stats = buildStatsService(List.of(
                tx("1", new BigDecimal("5.00"), Category.FOOD, LocalDate.now()),
                tx("2", new BigDecimal("3.00"), Category.FOOD, LocalDate.now()),
                tx("3", new BigDecimal("8.00"), Category.TRANSPORT, LocalDate.now())));
        assertThat(stats.countByCategory().get(Category.FOOD)).isEqualTo(2L);
        assertThat(stats.countByCategory().get(Category.TRANSPORT)).isEqualTo(1L);
    }

    @Test
    void totalByCategory_sumsPerCategory() {
        StatisticsService stats = buildStatsService(List.of(
                tx("1", new BigDecimal("3.00"), Category.FOOD, LocalDate.now()),
                tx("2", new BigDecimal("2.00"), Category.FOOD, LocalDate.now()),
                tx("3", new BigDecimal("7.00"), Category.TRANSPORT, LocalDate.now())));
        assertThat(stats.totalByCategory().get(Category.FOOD))
                .isEqualByComparingTo(new BigDecimal("5.00"));
        assertThat(stats.totalByCategory().get(Category.TRANSPORT))
                .isEqualByComparingTo(new BigDecimal("7.00"));
    }

    @Test
    void totalForPeriod_excludesOutOfRange() {
        StatisticsService stats = buildStatsService(List.of(
                tx("1", new BigDecimal("10.00"), Category.FOOD, LocalDate.of(2024, 3, 15)),
                tx("2", new BigDecimal("20.00"), Category.FOOD, LocalDate.of(2024, 8, 1))));
        BigDecimal total = stats.totalForPeriod(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 6, 30));
        assertThat(total).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void averageForPeriod_emptyPeriod_returnsZero() {
        StatisticsService stats = buildStatsService(List.of());
        BigDecimal avg = stats.averageForPeriod(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertThat(avg).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void averageForPeriod_roundsToTwoDecimalPlaces() {
        StatisticsService stats = buildStatsService(List.of(
                tx("1", new BigDecimal("10.00"), Category.FOOD, LocalDate.of(2024, 6, 1)),
                tx("2", new BigDecimal("10.00"), Category.FOOD, LocalDate.of(2024, 6, 2)),
                tx("3", new BigDecimal("10.00"), Category.FOOD, LocalDate.of(2024, 6, 3))));
        BigDecimal avg = stats.averageForPeriod(
                LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));
        assertThat(avg.scale()).isEqualTo(2);
        assertThat(avg).isEqualByComparingTo(new BigDecimal("10.00"));
    }

    @Test
    void grandTotal_sumsAll() {
        StatisticsService stats = buildStatsService(List.of(
                tx("1", new BigDecimal("100.00"), Category.FOOD, LocalDate.now()),
                tx("2", new BigDecimal("50.00"), Category.TRANSPORT, LocalDate.now())));
        assertThat(stats.grandTotal()).isEqualByComparingTo(new BigDecimal("150.00"));
    }
}