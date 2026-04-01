# Julius — Personal Finance API

## Core Principles
- **Simplicity First**: Make every change as simple as possible. Minimal code impact.
- **No Laziness**: Find root causes. No temporary fixes. Senior developer standards.
- **Demand Elegance**: For non-trivial changes, ask "is there a more elegant way?" Skip for simple fixes.

## Self-Improvement Loop
- After ANY correction from the user: update relevant rules/tasks with the pattern.
- Write rules for yourself that prevent the same mistake.
- Review lessons at session start.

## Verification Before Done
- Never mark a task complete without proving it works.
- Run tests, check logs, demonstrate correctness.
- Ask yourself: "Would a staff engineer approve this?"

## Project Stack & Conventions
- **Language**: Java 21
- **Framework**: Spring Boot 3.4.x
- **Build Tool**: Gradle (use `./gradlew`, NOT Maven)
- **Database**: MySQL (production), H2 (tests)
- **Migrations**: Flyway (`src/main/resources/db/migration/`)
- **Base Package**: `com.finance.app`
- **Architecture**: Clean Architecture (Domain → Application → Infrastructure → Web)
- **Lombok**: ALWAYS use Lombok annotations (`@Builder`, `@Getter`, `@Setter`, etc.)
- **No `var`**: Always declare types explicitly
- **Money**: `BigDecimal` with `RoundingMode.HALF_EVEN`, scale 2
- **Identity**: `UUID` for all primary keys
- **Tests**: JUnit 5 + Mockito, BDD style (Given/When/Then)
- **Null Checking**: Use `Objects.isNull()` / `Objects.nonNull()`, never `== null`
- **DTOs**: Java Records in `web/dto/request/` and `web/dto/response/`

## Test Commands (safe to auto-run)
```bash
# Run all tests (force clean to avoid caching)
./gradlew clean test --console=plain

# Run specific test class
./gradlew test --tests "com.finance.app.application.usecase.TransactionUseCaseTest" --console=plain
```

## Key Rules Files
- `.claude/agents/rules/instruction.md` — Coding standards (always loaded)
- `.claude/agents/rules/architecture_guidelines.md` — Architecture patterns (always loaded)
- `.claude/agents/tasks/` — Task definitions (feature specs)
- `.claude/skills/` — Reusable skill prompts (code-quality, jpa-patterns, etc.)

## H2 Test Database Gotchas
- Reserved keywords (`MONTH`, `YEAR`, `TYPE`, `STATUS`) must be escaped with backticks in `schema-test.sql`
- JPA `@Column` mappings must also use backticks: `@Column(name = "\`month\`")`
- H2 JDBC URL must include `MODE=MySQL` for MySQL compatibility
- When adding new NOT NULL columns to entities, update ALL test builders
