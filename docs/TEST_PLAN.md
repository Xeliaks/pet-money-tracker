# Test Plan — Wave 2

## Strategy

Unit tests are written at the class level with no Spring context or running server. Two distinct styles are used:

- **Black-box**: tests are derived from the public specification (method contracts, Javadoc). No knowledge of implementation internals is required.
- **White-box**: tests are derived from reading the implementation to ensure all branches are exercised (null checks, boundary conditions, empty-collection guards).

External dependencies (`StorageProvider`) are replaced with Mockito mocks. File I/O tests use JUnit 5 `@TempDir` for automatic cleanup.

---

## Test class inventory

| Test class | Production class | Style | Tests |
|---|---|---|---|
| `model/TransactionTest` | `Transaction` | Black-box | 5 |
| `model/CategoryTest` | `Category` | Black-box | 5 |
| `storage/JsonStorageProviderTest` | `JsonStorageProvider` | Black-box | 4 |
| `service/TransactionServiceTest` | `TransactionService` | Mixed | 16 |
| `service/StatisticsServiceTest` | `StatisticsService` | White-box | 6 |
| `service/ExportServiceTest` | `ExportService` | Black-box | 4 |
| `util/DateUtilsTest` | `DateUtils` | Black-box | 4 |
| `util/FormatUtilsTest` | `FormatUtils` | Black-box | 5 |

**Total: 49 tests**

---

## Black-box vs white-box breakdown — TransactionService

| Test method | Classification | Rationale |
|---|---|---|
| `add_returnsTransactionWithGeneratedId` | Black-box | Derived from method contract |
| `add_twoTransactions_idsAreDistinct` | Black-box | UUID uniqueness is a spec requirement |
| `add_callsSaveAll` | Black-box | Persistence is part of the contract |
| `findById_unknownId_throwsTransactionNotFoundException` | Black-box | Throws clause in Javadoc |
| `delete_removesTransactionFromList` | Black-box | Derived from method contract |
| `update_modifiesOnlyProvidedFields` | Black-box | Null-means-keep-existing is in Javadoc |
| `search_nullKeyword_returnsAll` | White-box | Guards `null \|\| blank` branch in `search()` |
| `search_blankKeyword_returnsAll` | White-box | Guards blank branch specifically |
| `search_matchesTitleOnly` | White-box | Covers title branch of OR in `contains()` |
| `search_matchesDescriptionOnly` | White-box | Covers description branch of OR |
| `search_noMatch_returnsEmpty` | White-box | Exercises no-match filter path |
| `filterByDateRange_onBoundaryDates_included` | White-box | Tests `!isBefore && !isAfter` boundary logic |
| `sortByAmount_descending_largestFirst` | White-box | Tests `ascending=false` branch |
| `sortByDate_ascending_oldestFirst` | White-box | Tests `ascending=true` branch |
| `filterByCategoryAndDateRange_categoryMismatch_excluded` | White-box | First filter fails, exercises short-circuit |
| `filterByCategoryAndDateRange_outOfRange_excluded` | White-box | Second filter fails after category passes |

---

## Coverage goal and exclusions

**Target:** ≥ 70% line coverage AND ≥ 70% branch coverage.

**Excluded from measurement** (configured in `app/build.gradle`):

| Package | Reason |
|---|---|
| `pet.money.tracker.cli.**` | Tightly coupled to `System.exit()` and console I/O |
| `pet.money.tracker.exception.**` | One-liners with no logic to measure |

Run `./gradlew test jacocoTestCoverageVerification` to confirm the gate passes.

---

## TDD evidence — commit hashes

The following three commits demonstrate the red-green-refactor cycle for `TransactionService.add()`:

| Phase | Commit | Description |
|---|---|---|
| Red | `b4b2ae5` | Failing test — `Assertions.fail("TDD red: implementation pending")` |
| Green | `421993d` | Test passes against existing implementation |
| Refactor | `8bed91a` | Extract `buildService(List<Transaction>)` helper |

Full TDD evidence will be referenced in `docs/TDD_EVIDENCE.md` during Wave 3.