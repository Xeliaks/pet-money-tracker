# Changelog

All notable changes to this project will be documented in this file.
Follows [Keep a Changelog](https://keepachangelog.com/en/1.1.0/) and [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Added
- Interactive REPL shell — run the app with no arguments for a persistent console

## [0.6.0] - 2026-05-30

### Added
- `Money` value object replacing raw `BigDecimal` for all amounts
- `AbstractExportStrategy` base class for export strategies
- Refactoring report (`docs/REFACTORING_REPORT.md`)

### Fixed
- `CsvExportStrategy` resource leak; `ObjectMapper` promoted to static field
- `StatisticsCacheObserver` was never registered — `StatisticsService` now owns it

## [0.5.0] - 2026-05-30

### Added
- Factory Method pattern for storage providers
- Strategy pattern for export (JSON/CSV) and list sorting (date/amount)
- Observer pattern for statistics cache invalidation
- Design patterns documentation (`docs/DESIGN_PATTERNS.md`)

## [0.4.0] - 2026-05-30

### Changed
- All static analysis tools set to `ignoreFailures = false`
- JaCoCo coverage gate added to CI pipeline
- Separate PMD ruleset for test sources

## [0.3.0] - 2026-05-29

### Added
- Cucumber BDD acceptance tests (5 scenarios)
- TDD evidence and agile estimation documents

## [0.2.0] - 2026-05-29

### Added
- 73 unit tests across model, service, storage, and util layers (95% line / 85% branch coverage)
- Test plan document

## [0.1.0] - 2026-05-29

### Added
- Gradle 9.2 project with Java 21 toolchain and dependency catalog
- PicoCLI CLI with 8 subcommands: add, list, update, delete, search, filter, export, stats
- JSON persistence at `~/.pet-tracker/transactions.json`
- Checkstyle, SpotBugs, PMD, JaCoCo configured
- GitHub Actions CI workflow and PR template
