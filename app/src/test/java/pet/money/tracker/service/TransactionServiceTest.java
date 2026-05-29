package pet.money.tracker.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pet.money.tracker.storage.StorageProvider;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private StorageProvider mockStorage;

    @Test
    void add_returnsTransactionWithGeneratedId() {
        Assertions.fail("TDD red: implementation pending");
    }
}