# Personal Expense Tracker

![CI](https://github.com/Xeliaks/pet-money-tracker/actions/workflows/ci.yml/badge.svg)

A command-line application for tracking personal expenses. Add, edit, delete, and categorise
transactions; compute summaries by period and category; export reports to JSON or CSV.

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

```bash
# Using the Gradle application plugin (development)
./gradlew run --args="--help"

# Or build a distribution and run the script
./gradlew installDist
./app/build/install/app/bin/app --help
```

---

## Available Commands

| Command    | Description                              | Key Options                                   |
|------------|------------------------------------------|-----------------------------------------------|
| `add`      | Add a new transaction                    | `-t` title, `-a` amount, `-c` category, `-d` date, `--desc` description |
| `list`     | List all transactions                    | `--sort-by date\|amount`, `--order asc\|desc`  |
| `update`   | Update a transaction by ID               | `--id`, then any field option to overwrite     |
| `delete`   | Delete a transaction by ID               | `--id`                                         |
| `search`   | Search transactions by keyword           | `--keyword`                                    |
| `filter`   | Filter by category and/or date range     | `--category`, `--from`, `--to`                 |
| `export`   | Export transactions to JSON or CSV       | `--format json\|csv`, `--output <path>`        |
| `stats`    | Show summary statistics                  | _(no required options)_                        |

### Examples

```bash
# Add a food expense
./gradlew run --args="add -t Lunch -a 12.50 -c FOOD -d 2026-05-29"

# List all, sorted by amount descending
./gradlew run --args="list --sort-by amount --order desc"

# Filter food expenses in May
./gradlew run --args="filter --category FOOD --from 2026-05-01 --to 2026-05-31"

# Export to CSV
./gradlew run --args="export --format csv --output expenses.csv"

# Show statistics
./gradlew run --args="stats"
```

### Categories

`FOOD` · `TRANSPORT` · `HOUSING` · `ENTERTAINMENT` · `HEALTH` · `EDUCATION` · `SHOPPING` · `UTILITIES` · `INCOME` · `OTHER`

---

## Data Storage

Transactions are stored in `~/.pet-tracker/transactions.json`. The directory is created
automatically on first run.

---

## Branching Strategy

| Branch | Purpose |
|---|---|
| `main` | Stable, tagged releases only |
| `develop` | Integration branch — all PRs target here |
| `feature/<name>` | Individual feature work |

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

For educational use.