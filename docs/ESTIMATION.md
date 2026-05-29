# Agile Estimation — Personal Expense Tracker

## Estimation Method

Story points use the **Fibonacci sequence** (1, 2, 3, 5, 8, 13, 21) to reflect the
exponential uncertainty of larger work items. Each point represents relative effort, not
calendar hours.

**Reference card used during planning poker:**

| Points | Meaning                                              |
|--------|------------------------------------------------------|
| 1      | Trivial — a few lines, no unknowns                   |
| 2      | Small — straightforward, well-understood             |
| 3      | Medium-small — clear scope, minor unknowns           |
| 5      | Medium — some design decisions required              |
| 8      | Medium-large — notable complexity or unknowns        |
| 13     | Large — multiple components, significant uncertainty |
| 21     | Very large — should be split if possible             |

---

## Product Backlog

| ID    | User Story                                                                                                  | Priority | Estimate (SP) | Actual (h) | Wave |
|-------|-------------------------------------------------------------------------------------------------------------|----------|---------------|------------|------|
| US-01 | As a user, I want to add, update and delete transactions so that I can maintain an accurate expense record  | High     | 5             | 2.5        | 1    |
| US-02 | As a user, I want to list and filter transactions by category and date so that I can review my spending      | High     | 5             | 2.0        | 1    |
| US-03 | As a user, I want to search transactions by keyword so that I can quickly find a specific expense            | Medium   | 3             | 1.0        | 1    |
| US-04 | As a user, I want to view spending statistics (totals, averages, category breakdown) so that I can track budget | Medium | 5             | 2.0        | 1    |
| US-05 | As a user, I want to export transactions to CSV and JSON so that I can share or process data externally      | Low      | 3             | 1.5        | 1    |
| US-06 | As a developer, I want an automated unit-test suite (≥70% line/branch coverage) so that regressions are caught immediately | High | 13        | 5.0        | 2    |
| US-07 | As a developer, I want BDD acceptance tests written in Gherkin so that business requirements are verified end-to-end | High | 8          | 3.0        | 3    |
| US-08 | As a developer, I want a CI/CD pipeline with enforced static analysis (Checkstyle, SpotBugs, PMD) so that code quality is maintained automatically | High | 8 | —     | 4    |
| US-09 | As a developer, I want Factory, Strategy, and Observer patterns implemented so that the code is maintainable and extensible | Medium | 13 | —     | 5    |
| US-10 | As a developer, I want identified code smells refactored and metrics reported so that long-term complexity is managed | Medium | 8 | —      | 6    |

**Total estimated:** 71 SP

---

## Sprint Plan

| Sprint   | Wave | User Stories      | Planned SP | Actual SP delivered |
|----------|------|-------------------|------------|---------------------|
| Sprint 1 | 1    | US-01–05          | 21         | 21                  |
| Sprint 2 | 2    | US-06             | 13         | 13                  |
| Sprint 3 | 3    | US-07             | 8          | 8                   |
| Sprint 4 | 4    | US-08             | 8          | —                   |
| Sprint 5 | 5    | US-09             | 13         | —                   |
| Sprint 6 | 6    | US-10             | 8          | —                   |

**Completed velocity (Sprints 1–3):** 21 → 13 → 8 SP  
**Average velocity:** 14 SP / sprint

---

## Estimation Accuracy (Completed Sprints)

| ID    | Estimate (SP) | Actual (h) | Notes                                                        |
|-------|---------------|------------|--------------------------------------------------------------|
| US-01 | 5             | 2.5        | Straightforward CRUD; PicoCLI annotation model simplified CLI work |
| US-02 | 5             | 2.0        | Stream filters are concise; date range edge cases were quick  |
| US-03 | 3             | 1.0        | Single `contains` predicate — less effort than estimated      |
| US-04 | 5             | 2.0        | GroupingBy collectors straightforward once Transaction model was stable |
| US-05 | 3             | 1.5        | Apache Commons CSV API well-documented; Jackson reuse helped  |
| US-06 | 13            | 5.0        | Mockito setup learning curve absorbed; white-box coverage analysis added time |
| US-07 | 8             | 3.0        | Cucumber JUnit Platform setup had one configuration gotcha (glue package) |

**Observation:** Estimates were consistently above actuals for Wave 1 stories (+50–100%
over-estimation), likely because the PicoCLI and Jackson libraries were more ergonomic than
anticipated. Wave 2 was more accurate (13 SP / 5 h ≈ 2.6 h/SP). Wave 3 tracked similarly.

---

## Definition of Done (all user stories)

- [ ] Code compiles with no errors
- [ ] All existing tests pass (`./gradlew test`)
- [ ] Coverage gates pass (`./gradlew jacocoTestCoverageVerification`)
- [ ] Static analysis shows zero violations (Wave 4+)
- [ ] Feature branch merged to `develop` via reviewed PR
- [ ] `CHANGELOG.md` updated