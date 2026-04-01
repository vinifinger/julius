# Skills

Skills are reusable prompts that teach AI assistants specific patterns for Java development.

## Structure Convention

Each skill folder contains:

| File | Purpose | Audience |
|------|---------|----------|
| `SKILL.md` | Instructions for the AI assistant | AI (loaded with `view`) 

## Available Skills

### Code Quality
| Skill | Description |
|-------|-------------|
| [code-quality](code-quality/) | Comprehensive Java code review — clean code, API contracts, null safety, exceptions, performance |

### Architecture & Design
| Skill | Description |
|-------|-------------|
| [design-patterns](design-patterns/) | Factory, Builder, Strategy, Observer, Decorator, Adapter with Java examples |

### Framework & Data
| Skill | Description |
|-------|-------------|
| [spring-boot](spring-boot/) | Spring Boot 3.x — REST APIs, JPA, Security, Testing. Includes detailed reference guides. |
| [jpa-patterns](jpa-patterns/) | JPA/Hibernate — N+1 detection, lazy loading, fetch strategies, transactions |
| [logging-patterns](logging-patterns/) | Structured logging (JSON), SLF4J, MDC, AI-friendly log formats |

## Adding a New Skill

### Before You Start

Validate your skill idea against existing skills:

- [ ] **No significant overlap** - Check the table above for similar skills
- [ ] **Clear scope** - Can be applied in one session (<15 checklist items)
- [ ] **Unique value** - What does it add that doesn't exist?

### Implementation Steps

1. Create folder: `.claude/skills/<skill-name>/`
2. Create `SKILL.md` with instructions for the AI assistant
3. Update this table
4. Update `CLAUDE.md` if relevant

## Usage

Skills are loaded based on context. When a task matches a skill's trigger keywords, the AI reads the SKILL.md file for guidance.

| Trigger | Skill Loaded |
|---------|--------------|
| "review this code" / "refactor" | `code-quality` |
| "implement strategy pattern" | `design-patterns` |
| "N+1 problem" / "lazy loading" | `jpa-patterns` |
| "add logging" / "debug this" | `logging-patterns` |
| "Spring Boot REST API" | `spring-boot` |
