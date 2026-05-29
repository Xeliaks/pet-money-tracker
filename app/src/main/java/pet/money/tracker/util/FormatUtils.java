package pet.money.tracker.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import pet.money.tracker.model.Transaction;

/** Utility methods for formatting values for CLI output. */
public final class FormatUtils {

    private static final int COL_ID = 8;
    private static final int COL_TITLE = 25;
    private static final int COL_AMOUNT = 12;
    private static final int COL_CATEGORY = 14;
    private static final int COL_DATE = 12;
    private static final int COL_DESC = 30;

    private static final NumberFormat AMOUNT_FORMAT =
            NumberFormat.getNumberInstance(Locale.US);

    static {
        AMOUNT_FORMAT.setMinimumFractionDigits(2);
        AMOUNT_FORMAT.setMaximumFractionDigits(2);
    }

    private FormatUtils() {
    }

    /**
     * Formats a monetary amount with thousands separator and 2 decimal places.
     *
     * @param amount the amount to format
     * @return formatted string, e.g. "1,234.56"
     */
    public static String formatAmount(BigDecimal amount) {
        return AMOUNT_FORMAT.format(amount);
    }

    /**
     * @return the column header row for the transaction table
     */
    public static String tableHeader() {
        return String.format("%-" + COL_ID + "s  %-" + COL_TITLE + "s  %" + COL_AMOUNT + "s  "
                + "%-" + COL_CATEGORY + "s  %-" + COL_DATE + "s  %-" + COL_DESC + "s",
                "ID", "TITLE", "AMOUNT", "CATEGORY", "DATE", "DESCRIPTION");
    }

    /**
     * @return a separator line matching the table header width
     */
    public static String tableSeparator() {
        int total = COL_ID + COL_TITLE + COL_AMOUNT + COL_CATEGORY + COL_DATE + COL_DESC + 12;
        return "-".repeat(total);
    }

    /**
     * Formats a transaction as a single padded table row.
     *
     * @param t the transaction to format
     * @return formatted row string
     */
    public static String formatTransaction(Transaction t) {
        String shortId = t.getId() != null && t.getId().length() > COL_ID
                ? t.getId().substring(0, COL_ID)
                : t.getId();
        String desc = t.getDescription() != null ? t.getDescription() : "";
        return String.format("%-" + COL_ID + "s  %-" + COL_TITLE + "s  %" + COL_AMOUNT + "s  "
                + "%-" + COL_CATEGORY + "s  %-" + COL_DATE + "s  %-" + COL_DESC + "s",
                shortId,
                truncate(t.getTitle(), COL_TITLE),
                formatAmount(t.getAmount()),
                t.getCategory(),
                DateUtils.format(t.getDate()),
                truncate(desc, COL_DESC));
    }

    private static String truncate(String value, int maxLen) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLen ? value : value.substring(0, maxLen - 1) + "…";
    }
}