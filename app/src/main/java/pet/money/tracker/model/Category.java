package pet.money.tracker.model;

import java.util.Arrays;

/** Expense categories available for classifying transactions. */
public enum Category {
    FOOD,
    TRANSPORT,
    HOUSING,
    ENTERTAINMENT,
    HEALTH,
    EDUCATION,
    SHOPPING,
    UTILITIES,
    INCOME,
    OTHER;

    /**
     * Converts a string to a Category, case-insensitively.
     * Returns {@code OTHER} for unrecognised values.
     *
     * @param value the string to convert
     * @return matching Category or OTHER
     */
    public static Category fromString(String value) {
        if (value == null || value.isBlank()) {
            return OTHER;
        }
        return Arrays.stream(values())
                .filter(c -> c.name().equalsIgnoreCase(value.trim()))
                .findFirst()
                .orElse(OTHER);
    }
}