package pet.money.tracker.model;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class MoneyTest {

    @Test
    void valueOf_null_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> Money.valueOf(null))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void valueOf_negativeAmount_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> Money.valueOf(new BigDecimal("-0.01")))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void valueOf_zero_isValid() {
        Money m = Money.valueOf(BigDecimal.ZERO);
        assertThat(m.value()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void valueOf_positive_isValid() {
        Money m = Money.valueOf(new BigDecimal("42.50"));
        assertThat(m.value()).isEqualByComparingTo(new BigDecimal("42.50"));
    }

    @Test
    void add_returnsCorrectSum() {
        Money a = Money.valueOf(new BigDecimal("10.00"));
        Money b = Money.valueOf(new BigDecimal("5.50"));
        assertThat(a.add(b).value()).isEqualByComparingTo(new BigDecimal("15.50"));
    }

    @Test
    void compareTo_smallerToLarger_returnsNegative() {
        assertThat(Money.valueOf(new BigDecimal("1.00"))
                .compareTo(Money.valueOf(new BigDecimal("2.00")))).isNegative();
    }

    @Test
    void compareTo_equal_returnsZero() {
        assertThat(Money.valueOf(new BigDecimal("5.00"))
                .compareTo(Money.valueOf(new BigDecimal("5.00")))).isZero();
    }

    @Test
    void compareTo_largerToSmaller_returnsPositive() {
        assertThat(Money.valueOf(new BigDecimal("3.00"))
                .compareTo(Money.valueOf(new BigDecimal("1.00")))).isPositive();
    }

    @Test
    void display_formatsWithTwoDecimalPlaces() {
        assertThat(Money.valueOf(new BigDecimal("5")).display()).isEqualTo("5.00");
    }

    @Test
    void display_formatsWithThousandsSeparator() {
        assertThat(Money.valueOf(new BigDecimal("1234.50")).display()).isEqualTo("1,234.50");
    }

    @Test
    void jackson_roundTrip_preservesValue() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Money original = Money.valueOf(new BigDecimal("42.50"));
        String json = mapper.writeValueAsString(original);
        Money parsed = mapper.readValue(json, Money.class);
        assertThat(parsed.value()).isEqualByComparingTo(original.value());
    }
}
