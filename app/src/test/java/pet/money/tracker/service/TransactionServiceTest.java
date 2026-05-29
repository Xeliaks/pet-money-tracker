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
class TransactionServiceTest {

    @Mock
    private StorageProvider mockStorage;

    private TransactionService buildService(List<Transaction> initial) {
        when(mockStorage.loadAll()).thenReturn(new ArrayList<>(initial));
        return new TransactionService(mockStorage);
    }

    @Test
    void add_returnsTransactionWithGeneratedId() {
        TransactionService service = buildService(List.of());
        Transaction result = service.add("Coffee", new BigDecimal("3.50"), Category.FOOD,
                LocalDate.of(2024, 1, 15), "morning coffee");
        assertThat(result.getId()).isNotNull().isNotBlank();
    }
}