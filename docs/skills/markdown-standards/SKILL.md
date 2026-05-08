---
name: markdown-standards
description: Standards for creating and maintaining markdown documentation in Julius. Ensures compatibility with markdownlint and clean rendering. Use whenever creating or editing .md files.
---

# Markdown Standards Skill

Guidelines for high-quality, lint-free markdown documentation.

## Core Rules (markdownlint compatible)

### 1. Headers (MD001, MD018, MD022, MD025, MD026, MD041)
- **First line:** Must be a level 1 header (`# Title`).
- **Hierarchy:** Do not skip header levels (e.g., `#` then `###`).
- **Spacing:** Headers must be surrounded by blank lines.
- **Punctuation:** Do not end headers with punctuation (e.g., `?`, `!`, `.`).
- **Unique:** Only one level 1 header per document.

### 2. Spacing & Newlines (MD012, MD031, MD032, MD047)
- **Code Blocks:** Fenced code blocks must be surrounded by blank lines.
- **Lists:** Lists must be surrounded by blank lines.
- **Blank Lines:** No more than one consecutive blank line.
- **End of File:** Files must end with exactly one newline character.

### 3. Content Formatting (MD013, MD010, MD033, MD040)
- **Line Length:** Wrap lines at ~80 characters for better readability in 
  terminal-based tools and IDEs.
- **No Tabs:** Use spaces instead of hard tabs.
- **No HTML:** Avoid inline HTML; use pure markdown syntax.
- **Code Language:** Always specify the language for fenced code blocks.

### 4. Lists & Links (MD004, MD007, MD029, MD039, MD042)
- **Unordered Lists:** Use dashes (`-`) for list items.
- **Indentation:** Use 2 spaces for nested list items.
- **Ordered Lists:** Use `1.`, `2.`, `3.` (prefix increments).
- **Links:** Do not use empty links or spaces inside link text.

## Example of Good Markdown

```markdown
# Documentation Title

This is a paragraph with a [link](https://example.com). It is wrapped at around
80 characters to keep it readable in all editors.

## Section One

- Item one
- Item two
  - Sub-item with 2 spaces

```java
public class Example {}
```

Final thought on the section.
```
