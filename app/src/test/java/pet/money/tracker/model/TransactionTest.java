package pet.money.tracker.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TransactionTest {

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
        assertThat(make("id-1")).isEqualTo(make("id-1"));
    }

    @Test
    void equals_differentId_notEqual() {
        assertThat(make("id-1")).isNotEqualTo(make("id-2"));
    }

    @Test
    void hashCode_consistentWithEquals() {
        assertThat(make("id-1").hashCode()).isEqualTo(make("id-1").hashCode());
    }

    @Test
    void toString_isNotNull() {
        assertThat(make("id-1").toString()).isNotNull().contains("id-1");
    }
}