# Personal Expense Tracker

![CI](https://github.com/Xeliaks/pet-money-tracker/actions/workflows/ci.yml/badge.svg)

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Gradle](https://img.shields.io/badge/Gradle-9.2-02303A?logo=gradle&logoColor=white)
![JUnit5](https://img.shields.io/badge/JUnit_5-5.12-25A162?logo=junit5&logoColor=white)
![Cucumber](https://img.shields.io/badge/Cucumber-7.21-23D96C?logo=cucumber&logoColor=white)
![GitHub Actions](https://img.shields.io/badge/GitHub_Actions-CI%2FCD-2088FF?logo=github-actions&logoColor=white)
![JaCoCo](https://img.shields.io/badge/JaCoCo-0.8.12-C21325?logo=jacoco&logoColor=white)
![Checkstyle](https://img.shields.io/badge/Checkstyle-10.21-4D9900)
![SpotBugs](https://img.shields.io/badge/SpotBugs-4.9-4B0082)
![PMD](https://img.shields.io/badge/PMD-7.9-FF6600)

A command-line application for tracking personal expenses. Add, edit, delete, and categorise
transactions; filter and search by keyword, category or date range; export to JSON or CSV;
and view summary statistics.

---

## Requirements

- Java 21 (the Gradle wrapper downloads it automatically via the Foojay toolchain resolver)
- Git

---

## Build

```bash
./gradlew build        # compile, test, static analysis, coverage check
./gradlew test         # run tests only
./gradlew check        # all verification tasks (tests + static analysis + coverage)
```

On Windows use `gradlew.bat` instead of `./gradlew`.

---

## Run

**Interactive shell** — run with no arguments to open a persistent console:

```bash
./gradlew installDist
./app/build/install/app/bin/app          # Linux/macOS
app\build\install\app\bin\app.bat        # Windows
```

```
expense-tracker shell — type 'help' for commands, 'exit' to quit.
> add -t "Coffee" -a 3.50 -c FOOD
> list
> exit
```

**One-shot commands** — pass a subcommand directly:

```bash
./gradlew run --args="--help"
./gradlew run --args="add -t Lunch -a 12.50 -c FOOD"
```

---

## Commands

| Command  | Description                          | Key Options                                                    |
|----------|--------------------------------------|----------------------------------------------------------------|
| `add`    | Add a new transaction                | `-t` title, `-a` amount, `-c` category, `-d` date, `--desc`   |
| `list`   | List all transactions                | `--sort-by date\|amount`, `--order asc\|desc`                  |
| `update` | Update a transaction by ID           | `--id`, then any field option to overwrite                     |
| `delete` | Delete a transaction by ID           | `--id`                                                         |
| `search` | Search transactions by keyword       | `--keyword`                                                    |
| `filter` | Filter by category and/or date range | `--category`, `--from`, `--to`                                 |
| `export` | Export transactions to JSON or CSV   | `--format json\|csv`, `--output <path>`                        |
| `stats`  | Show summary statistics              |                                                                |

### Categories

`FOOD` · `TRANSPORT` · `HOUSING` · `ENTERTAINMENT` · `HEALTH` · `EDUCATION` · `SHOPPING` · `UTILITIES` · `INCOME` · `OTHER`

### Examples

```bash
# Add an expense
./gradlew run --args="add -t Lunch -a 12.50 -c FOOD -d 2026-05-29"

# List all, sorted by amount descending
./gradlew run --args="list --sort-by amount --order desc"

# Filter food expenses in May
./gradlew run --args="filter --category FOOD --from 2026-05-01 --to 2026-05-31"

# Export to CSV
./gradlew run --args="export --format csv --output expenses.csv"
```

---

## Data Storage

Transactions are stored in `~/.pet-tracker/transactions.json`. The directory is created
automatically on first run.

---

## Branching Strategy

| Branch           | Purpose                                  |
|------------------|------------------------------------------|
| `main`           | Stable, tagged releases only             |
| `develop`        | Integration branch — all PRs target here |
| `feature/<name>` | Individual feature work                  |
| `fix/<name>`     | Bug fixes                                |

Releases are tagged on `main` and published as GitHub Releases.

---

## Project Documentation

| Document | Description |
|---|---|
| [docs/TEST_PLAN.md](docs/TEST_PLAN.md) | Testing strategy, black-box vs white-box breakdown, coverage targets |
| [docs/TDD_EVIDENCE.md](docs/TDD_EVIDENCE.md) | Red-green-refactor commit evidence |
| [docs/ESTIMATION.md](docs/ESTIMATION.md) | User story estimates and actual effort |
| [docs/DESIGN_PATTERNS.md](docs/DESIGN_PATTERNS.md) | GoF patterns used, UML diagrams, rationale |
| [docs/REFACTORING_REPORT.md](docs/REFACTORING_REPORT.md) | Code smells, refactoring techniques, before/after metrics |
| [CHANGELOG.md](CHANGELOG.md) | Version history |

---

## License

MIT License — Copyright (c) 2026 Xeliaks