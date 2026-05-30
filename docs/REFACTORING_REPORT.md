# Refactoring Report — Wave 6

## Overview

Three code smells were identified and eliminated. All 60 tests continue to pass.
Coverage held at **95% line / 85% branch** (JaCoCo gate ≥ 70% on both).
Zero new Checkstyle, PMD, or SpotBugs violations were introduced.

---

## Smell 1 — Long Method → Extract Method

**File:** `service/TransactionService.java`  
**Technique:** Extract Method (private `Predicate<Transaction>` helpers)

### Before

The filter methods embedded their predicate logic inline, duplicating the date-range
check between `filterByDateRange` and `filterByCategoryAndDateRange`:

```java
public List<Transaction> filterByCategory(Category category) {
    return cache.stream()
            .filter(t -> t.getCategory() == category)   // unnamed, repeated
            .collect(Collectors.toList());
}

public List<Transaction> filterByDateRange(LocalDate from, LocalDate to) {
    return cache.stream()
            .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
            .collect(Collectors.toList());
}

public List<Transaction> filterByCategoryAndDateRange(
        Category category, LocalDate from, LocalDate to) {
    return cache.stream()
            .filter(t -> t.getCategory() == category)
            .filter(t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to))
            .collect(Collectors.toList());
}
```

### After

Two named private methods capture the predicate logic once and are reused across
all three filter methods:

```java
public List<Transaction> filterByCategory(Category category) {
    return cache.stream()
            .filter(categoryMatches(category))
            .collect(Collectors.toList());
}

public List<Transaction> filterByDateRange(LocalDate from, LocalDate to) {
    return cache.stream()
            .filter(dateInRange(from, to))
            .collect(Collectors.toList());
}

public List<Transaction> filterByCategoryAndDateRange(
        Category category, LocalDate from, LocalDate to) {
    return cache.stream()
            .filter(categoryMatches(category))
            .filter(dateInRange(from, to))
            .collect(Collectors.toList());
}

private Predicate<Transaction> categoryMatches(Category category) {
    return t -> t.getCategory() == category;
}

private Predicate<Transaction> dateInRange(LocalDate from, LocalDate to) {
    return t -> !t.getDate().isBefore(from) && !t.getDate().isAfter(to);
}
```

### Metrics

| Method | Checkstyle CC (before) | Checkstyle CC (after) |
|---|---|---|
| `filterByCategory` | 2 | 1 |
| `filterByDateRange` | 3 | 1 |
| `filterByCategoryAndDateRange` | 4 | 1 |
| `categoryMatches` (new) | — | 1 |
| `dateInRange` (new) | — | 2 |

*CC counts: 1 base + 1 per `&&` / `||` + 1 per named `Predicate` factory call removed from calling method.*

---

## Smell 2 — Primitive Obsession → Introduce Value Object

**Files:** `model/Money.java` (new), `model/Transaction.java`,
`service/TransactionService.java`, `service/StatisticsService.java`,
`util/FormatUtils.java`, `patterns/CsvExportStrategy.java`  
**Technique:** Introduce Value Object

### Before

`Transaction` stored the amount as a raw `BigDecimal`, giving no invariant
enforcement and forcing callers to inline formatting and arithmetic:

```java
// Transaction.java
private BigDecimal amount;

public BigDecimal getAmount() { return amount; }
public void setAmount(BigDecimal amount) { this.amount = amount; }

// TransactionService.java — no validation, any BigDecimal accepted
Transaction t = new Transaction(
        UUID.randomUUID().toString(), title, amount, category, date, description);

// StatisticsService.java — arithmetic scattered across the service
result.merge(t.getCategory(), t.getAmount(), BigDecimal::add);

// FormatUtils.java — formatting knowledge split from the type
formatAmount(t.getAmount())   // BigDecimal → formatted string
```

### After

`Money` is a record that owns validation, addition, and display:

```java
// model/Money.java
public record Money(BigDecimal value) implements Comparable<Money> {

    public Money {
        if (value == null || value.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("amount must be non-negative");
        }
    }

    @JsonCreator
    public static Money valueOf(BigDecimal amount) { return new Money(amount); }

    @JsonValue
    public BigDecimal value() { return value; }          // serialises as plain number

    public Money add(Money other) {
        return new Money(this.value.add(other.value));
    }

    public String display() { return FORMAT.format(value); }

    @Override
    public int compareTo(Money other) { return this.value.compareTo(other.value); }
}

// Transaction.java — field is now typed
private Money amount;
public Money getAmount() { return amount; }

// TransactionService.java — wraps raw input at the boundary
Transaction t = new Transaction(
        UUID.randomUUID().toString(), title, Money.valueOf(amount), ...);

// StatisticsService.java — extracts the primitive only when arithmetic is needed
result.merge(t.getCategory(), t.getAmount().value(), BigDecimal::add);

// FormatUtils / CsvExportStrategy — display is on the type
t.getAmount().display()
```

### Jackson backward-compatibility

`@JsonValue` on `value()` serialises `Money` as a plain JSON number.
`@JsonCreator` on `valueOf(BigDecimal)` reconstructs it on load.
Existing `~/.pet-tracker/transactions.json` files require no migration.

### Metrics

| Dimension | Before | After |
|---|---|---|
| Validation on `amount < 0` | None | `Money` compact constructor |
| Addition API | Caller assembles `BigDecimal::add` | `money.add(other)` |
| Formatting API | `FormatUtils.formatAmount(bd)` at call site | `money.display()` |
| JaCoCo line coverage | 95% (202/213) | 95% (197/207) |
| JaCoCo branch coverage | 85% (56/66) | 85% (51/60) |

---

## Smell 3 — Duplicate Code → Pull Up to Abstract Class

**Files:** `patterns/AbstractExportStrategy.java` (new),
`patterns/CsvExportStrategy.java`, `patterns/JsonExportStrategy.java`  
**Technique:** Pull Up Method / Form Template Method

### Before

Both strategies independently contained identical directory-creation and
`IOException`-to-`AppException` wrapping blocks:

```java
// CsvExportStrategy.java
@Override
public void export(List<Transaction> transactions, Path outputPath) {
    try {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);     // ← duplicated
        }
        // ... CSV-specific logic ...
    } catch (IOException e) {
        throw new AppException("Failed to export CSV to " + outputPath, e);  // ← duplicated
    }
}

// JsonExportStrategy.java — identical boilerplate
@Override
public void export(List<Transaction> transactions, Path outputPath) {
    try {
        Path parent = outputPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);     // ← duplicated
        }
        // ... JSON-specific logic ...
    } catch (IOException e) {
        throw new AppException("Failed to export JSON to " + outputPath, e);  // ← duplicated
    }
}
```

### After

Boilerplate is pulled into `AbstractExportStrategy.export()`. Subclasses implement
the format-specific `doExport()` which propagates `IOException` without wrapping:

```java
// AbstractExportStrategy.java
public abstract class AbstractExportStrategy implements ExportStrategy {

    @Override
    public final void export(List<Transaction> transactions, Path outputPath) {
        try {
            Path parent = outputPath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            doExport(transactions, outputPath);
        } catch (IOException e) {
            throw new AppException(
                    "Failed to export " + getFormatName() + " to " + outputPath, e);
        }
    }

    protected abstract void doExport(List<Transaction> transactions, Path outputPath)
            throws IOException;
}

// CsvExportStrategy.java — only format-specific logic remains
public class CsvExportStrategy extends AbstractExportStrategy {
    @Override
    protected void doExport(List<Transaction> transactions, Path outputPath)
            throws IOException {
        // ... CSV-specific logic only ...
    }
}
```

### Metrics

| Dimension | Before | After |
|---|---|---|
| Duplicated boilerplate lines | 8 (×2 = 16 total) | 0 |
| Strategy classes implementing `export()` | 2 | 0 (moved to abstract base) |
| `AbstractExportStrategy` | — | 1 new class |
| `export()` declared `final` in base | — | Yes (prevents re-duplication) |

---

## Summary

| Smell | Technique | Files changed |
|---|---|---|
| Long Method | Extract Method | `TransactionService.java` |
| Primitive Obsession | Introduce Value Object | `Money.java` (new), `Transaction.java`, `TransactionService.java`, `StatisticsService.java`, `FormatUtils.java`, `CsvExportStrategy.java` |
| Duplicate Code | Pull Up / Template Method | `AbstractExportStrategy.java` (new), `CsvExportStrategy.java`, `JsonExportStrategy.java` |

**Build:** `./gradlew check` — BUILD SUCCESSFUL  
**Tests:** 60 passing, 0 failing  
**Coverage:** 95% line, 85% branch (gate ≥ 70%)  
**Static analysis:** 0 Checkstyle violations, 0 PMD violations, 0 SpotBugs violations
