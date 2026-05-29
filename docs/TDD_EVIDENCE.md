# TDD Evidence — Wave 2

This document records the Red → Green → Refactor cycle applied during Wave 2 unit testing.

---

## Chosen Test Case

**`TransactionServiceTest.add_returnsTransactionWithGeneratedId`**

This test was selected as the TDD anchor because `TransactionService.add()` is the
most critical production path: it validates construction, UUID generation, and cache/persist
interaction in one shot.

---

## The Three Commits

### 1. Red — test fails because implementation is missing

**Commit:** `b4b2ae5`  
**Message:** `test(tdd): red - add_returnsTransactionWithGeneratedId`

The test was written first, with a deliberate failure to prove it runs (and fails) before
any production code exists:

```java
@Test
void add_returnsTransactionWithGeneratedId() {
    Assertions.fail("TDD red: implementation pending");
}
```

The build output at this point shows:

```
TransactionServiceTest > add_returnsTransactionWithGeneratedId FAILED
    org.opentest4j.AssertionFailedError: TDD red: implementation pending
```

---

### 2. Green — minimal implementation to make the test pass

**Commit:** `421993d`  
**Message:** `test(tdd): green - minimal TransactionService.add() implementation`

The failing assertion was replaced with the real assertion against `TransactionService.add()`.
`TransactionService` already had a stub `add()` that returned `null`; it was replaced with
the UUID-generating implementation in the same commit:

```java
@Test
void add_returnsTransactionWithGeneratedId() {
    TransactionService service = buildService(List.of());
    Transaction result = service.add("Coffee", new BigDecimal("3.50"), Category.FOOD,
            LocalDate.of(2024, 1, 15), "morning coffee");
    assertThat(result.getId()).isNotNull().isNotBlank();
}
```

All tests pass at this point, but the test class contains duplicated service-construction code.

---

### 3. Refactor — eliminate duplication without changing behaviour

**Commit:** `8bed91a`  
**Message:** `refactor: extract persist() helper in TransactionService`

A private `buildService(List<Transaction>)` helper was extracted into `TransactionServiceTest`
to remove the repeated `when(mockStorage.loadAll()).thenReturn(...)` setup. No production logic
changed; all 19 `TransactionServiceTest` methods still pass after the extraction.

```java
// Before — inline in every test
when(mockStorage.loadAll()).thenReturn(new ArrayList<>(List.of()));
TransactionService service = new TransactionService(mockStorage);

// After — single helper
private TransactionService buildService(List<Transaction> initial) {
    when(mockStorage.loadAll()).thenReturn(new ArrayList<>(initial));
    return new TransactionService(mockStorage);
}
```

---

## Why TDD

| Stage    | Purpose                                                              |
|----------|----------------------------------------------------------------------|
| Red      | Prove the test is wired up and will catch missing implementation     |
| Green    | Write the *minimum* production code to satisfy the specification     |
| Refactor | Improve design and remove duplication while keeping all tests green  |

The cycle enforces that tests are written from the specification, not reverse-engineered
from code and that every production line is motivated by a failing test.

---

## Wave 2 Coverage (post-TDD)

| Metric | Result | Gate |
|--------|--------|------|
| Line   | 95%    | ≥70% |
| Branch | 85%    | ≥70% |

55 tests across 7 test classes — all passing.