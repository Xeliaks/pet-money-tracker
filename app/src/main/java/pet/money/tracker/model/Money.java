package pet.money.tracker.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

/** Immutable value object representing a non-negative monetary amount. */
public record Money(BigDecimal value) implements Comparable<Money> {

    private static final NumberFormat FORMAT = NumberFormat.getNumberInstance(Locale.US);

    static {
        FORMAT.setMinimumFractionDigits(2);
        FORMAT.setMaximumFractionDigits(2);
    }

    /**
     * Compact constructor — validates that the amount is non-null and non-negative.
     *
     * @param value the raw decimal amount
     */
    public Money {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
    }

    /**
     * Factory method used by Jackson to deserialize a plain JSON number into a {@code Money}.
     *
     * @param amount raw decimal value read from JSON
     * @return a new Money wrapping {@code amount}
     */
    @JsonCreator
    public static Money valueOf(BigDecimal amount) {
        return new Money(amount);
    }

    /**
     * Returns the underlying decimal value.
     * The {@code @JsonValue} annotation causes Jackson to serialize this Money
     * as a plain number, preserving the existing JSON file format.
     *
     * @return the raw BigDecimal
     */
    @JsonValue
    public BigDecimal value() {
        return value;
    }

    /**
     * Returns a new Money whose value is the sum of this and {@code other}.
     *
     * @param other addend
     * @return sum
     */
    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }

    /**
     * Returns a display string suitable for CLI output,
     * e.g. {@code "1,234.56"}.
     *
     * @return formatted amount string
     */
    public String display() {
        return FORMAT.format(value);
    }

    @Override
    public int compareTo(Money other) {
        return this.value.compareTo(other.value);
    }
}
