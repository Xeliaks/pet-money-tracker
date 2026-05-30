package pet.money.tracker.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TransactionTest {

    private static final String TX_ID = "id-1";

    private Transaction make(String id) {
        return new Transaction(id, "Coffee", new BigDecimal("3.50"),
                Category.FOOD, LocalDate.of(2024, 1, 15), "morning");
    }

    @Test
    void constructor_setsAllFields() {
        Transaction t = make("abc-123");
        assertThat(t.getId()).isEqualTo("abc-123");
        assertThat(t.getTitle()).isEqualTo("Coffee");
        assertThat(t.getAmount()).isEqualByComparingTo(new BigDecimal("3.50"));
        assertThat(t.getCategory()).isEqualTo(Category.FOOD);
        assertThat(t.getDate()).isEqualTo(LocalDate.of(2024, 1, 15));
        assertThat(t.getDescription()).isEqualTo("morning");
    }

    @Test
    void equals_sameId_isEqual() {
        assertThat(make(TX_ID)).isEqualTo(make(TX_ID));
    }

    @Test
    void equals_differentId_notEqual() {
        assertThat(make(TX_ID)).isNotEqualTo(make("id-2"));
    }

    @Test
    void hashCode_consistentWithEquals() {
        assertThat(make(TX_ID).hashCode()).isEqualTo(make(TX_ID).hashCode());
    }

    @Test
    void toString_containsId() {
        assertThat(make(TX_ID).toString()).isNotNull().contains(TX_ID);
    }
}
