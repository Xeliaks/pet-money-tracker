# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

## [0.1.0] - 2026-05-29

### Added
- Gradle 9.2.0 project setup with Java 21 toolchain
- Dependency version catalog (`gradle/libs.versions.toml`) with PicoCLI, Jackson,
  Commons CSV, AssertJ, Mockito, Cucumber
- JaCoCo code coverage (70% line + branch threshold)
- Checkstyle static analysis (Google-style, 120-char line limit)
- SpotBugs bug detection with custom exclude filter
- PMD code quality ruleset
- GitHub Actions CI workflow (push/PR on main and develop)
- Pull request template with quality checklist
- Skeleton package structure: `cli`, `model`, `service`, `storage`, `patterns`, `util`, `exception`
- README with build/run instructions and branching strategy
- CHANGELOG following Keep a Changelog format