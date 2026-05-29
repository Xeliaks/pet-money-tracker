package pet.money.tracker.util;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import pet.money.tracker.model.Category;
import pet.money.tracker.model.Transaction;

class FormatUtilsTest {

    @Test
    void formatAmount_includesThousandsSeparator() {
        String result = FormatUtils.formatAmount(new BigDecimal("1234.00"));
        assertThat(result).isEqualTo("1,234.00");
    }

    @Test
    void formatAmount_twoDecimalPlaces() {
        String result = FormatUtils.formatAmount(new BigDecimal("5"));
        assertThat(result).endsWith(".00");
    }

    @Test
    void formatTransaction_returnsNonNull() {
        Transaction t = new Transaction("abc12345", "Coffee", new BigDecimal("3.50"),
                Category.FOOD, LocalDate.of(2024, 1, 15), null);
        assertThat(FormatUtils.formatTransaction(t)).isNotNull();
    }

    @Test
    void tableHeader_returnsNonNull() {
        assertThat(FormatUtils.tableHeader()).isNotNull().isNotBlank();
    }

    @Test
    void tableSeparator_returnsNonNull() {
        assertThat(FormatUtils.tableSeparator()).isNotNull().isNotBlank();
    }
}