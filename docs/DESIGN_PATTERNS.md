# Design Patterns

Four GoF patterns are applied in this project: one Creational and three Behavioural.

---

## 1. Factory Method (Creational)

### Problem solved
`MainCommand` originally hardcoded `new JsonStorageProvider(path)`.
Adding a second storage format (e.g. SQLite) would require touching the bootstrapping code directly, violating the Open/Closed Principle.

### Solution
An abstract `StorageProviderFactory` declares the `createProvider(Path)` factory method.
`JsonStorageProviderFactory` is the only concrete factory today.
`MainCommand` calls `StorageProviderFactory.forFormat("json").createProvider(path)` — the bootstrapping code never names a concrete provider class.

### Files
| Role | File |
|---|---|
| Abstract creator | `patterns/StorageProviderFactory.java` |
| Concrete creator | `patterns/JsonStorageProviderFactory.java` |
| Product interface | `storage/StorageProvider.java` |
| Concrete product | `storage/JsonStorageProvider.java` |
| Client | `cli/MainCommand.java` |

### ASCII UML

```
StorageProviderFactory (abstract)
  + forFormat(format) : StorageProviderFactory   <<static>>
  + createProvider(path) : StorageProvider        <<abstract>>
        ^
        |
JsonStorageProviderFactory
  + createProvider(path) : StorageProvider
              |
              v
        JsonStorageProvider  (implements StorageProvider)
```

---

## 2. Strategy — Export (Behavioural)

### Problem solved
`ExportService` originally contained the full JSON and CSV serialisation logic inside two large methods.
Adding a third format (e.g. XML) required modifying `ExportService` and risked breaking existing formats.

### Solution
`ExportStrategy` (pre-existing stub interface) defines `export(List<Transaction>, Path)`.
`JsonExportStrategy` and `CsvExportStrategy` each encapsulate one format's logic.
`ExportContext` holds a strategy and delegates the call via `execute()`.
`ExportService` becomes a thin facade: each public method instantiates the right strategy and hands it to a fresh `ExportContext`.

### Files
| Role | File |
|---|---|
| Strategy interface | `patterns/ExportStrategy.java` |
| Concrete strategy — JSON | `patterns/JsonExportStrategy.java` |
| Concrete strategy — CSV | `patterns/CsvExportStrategy.java` |
| Context | `patterns/ExportContext.java` |
| Facade (uses context) | `service/ExportService.java` |
| Client | `cli/ExportCommand.java` |

### ASCII UML

```
        ExportContext
          - strategy : ExportStrategy
          + execute(txs, path)
                |
                | delegates to
                v
        <<interface>> ExportStrategy
          + export(txs, path)
          + getFormatName() : String
            /           \
           /             \
JsonExportStrategy   CsvExportStrategy
```

---

## 3. Strategy — Sort (Behavioural)

### Problem solved
`ListCommand` used conditional `if/else` to call `TransactionService.sortByDate` or `sortByAmount`.
Each new sort criterion (e.g. sort by title) would require another branch in the command.

### Solution
`SortStrategy` (pre-existing stub interface) defines `sort(List<Transaction>) : List<Transaction>`.
`SortByDate` and `SortByAmount` encapsulate their comparator logic.
`ListCommand` selects the concrete strategy based on the `--sort-by` flag and calls `strategy.sort(all)` — no conditional on the format-specific path.
`TransactionService.sortByDate/Amount` also delegate to the same strategies, ensuring consistent behaviour.

### Files
| Role | File |
|---|---|
| Strategy interface | `patterns/SortStrategy.java` |
| Concrete strategy — date | `patterns/SortByDate.java` |
| Concrete strategy — amount | `patterns/SortByAmount.java` |
| Client | `cli/ListCommand.java` |
| Also uses strategies | `service/TransactionService.java` |

### ASCII UML

```
        <<interface>> SortStrategy
          + sort(txs) : List<Transaction>
            /           \
           /             \
      SortByDate      SortByAmount
        - ascending     - ascending
        + sort(...)     + sort(...)

  ListCommand selects strategy at runtime:
    strategy = "amount" ? new SortByAmount(asc) : new SortByDate(asc)
    result   = strategy.sort(all)
```

---

## 4. Observer (Behavioural)

### Problem solved
After an add, update, or delete, any interested component (e.g. a statistics cache) needs to know that the transaction list changed.
Coupling `TransactionService` directly to `StatisticsService` would create a circular dependency and make the service hard to extend.

### Solution
`TransactionObserver` (pre-existing stub interface) defines `onTransactionChanged(TransactionEvent)`.
`TransactionEvent` (pre-existing stub record) carries the `EventType` (ADDED / UPDATED / DELETED) and the affected `Transaction`.
`TransactionService` maintains a `List<TransactionObserver>` and fires an event after every mutation via a private `notifyObservers()` helper.
`StatisticsCacheObserver` implements the interface and sets a `dirty` flag whenever a mutation occurs, signalling that any cached statistics must be recomputed.
`MainCommand` registers one `StatisticsCacheObserver` on startup.

### Files
| Role | File |
|---|---|
| Observer interface | `patterns/TransactionObserver.java` |
| Event record | `patterns/TransactionEvent.java` |
| Concrete observer | `patterns/StatisticsCacheObserver.java` |
| Subject (notifier) | `service/TransactionService.java` |
| Wiring | `cli/MainCommand.java` |

### ASCII UML

```
TransactionService  (Subject)
  - observers : List<TransactionObserver>
  + addObserver(observer)
  - notifyObservers(event)      fires after add / update / delete
        |
        | notifies
        v
<<interface>> TransactionObserver
  + onTransactionChanged(event)
        ^
        |
StatisticsCacheObserver
  - dirty : boolean
  + onTransactionChanged(event)   sets dirty = true
  + isDirty() : boolean
  + markClean()

TransactionEvent (record)
  - type : EventType  { ADDED, UPDATED, DELETED }
  - transaction : Transaction
```
