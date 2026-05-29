package pet.money.tracker.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import pet.money.tracker.exception.ValidationException;

class DateUtilsTest {

    @Test
    void parse_validIsoDate_returnsLocalDate() {
        LocalDate result = DateUtils.parse("2024-06-15");
        assertThat(result).isEqualTo(LocalDate.of(2024, 6, 15));
    }

    @Test
    void parse_invalidFormat_throwsValidationException() {
        assertThatThrownBy(() -> DateUtils.parse("15/06/2024"))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void format_returnsDisplayPattern() {
        String result = DateUtils.format(LocalDate.of(2024, 6, 15));
        assertThat(result).isEqualTo("15/06/2024");
    }

    @Test
    void today_returnsNonNull() {
        assertThat(DateUtils.today()).isNotNull();
    }
}