---
description: Implement GitHub Actions Best Practices via GitHub MCP
---

# Task: Implement GitHub Actions Best Practices

## Objective
Upgrade the current CI pipeline into an enterprise-grade GitHub Actions setup for the Julius application. Follow the predefined implementation plan and execute the changes purely via the `github-mcp-server` integration.

## Context
The repository currently has a basic `.github/workflows/ci.yml` that compiles Java 21, sets up Gradle, and runs a Docker build. The goal is to enhance this pipeline by applying concurrency, isolated stage jobs, CodeQL, JaCoCo analytics, and Dependabot.

## Agent Instructions

You are expected to utilize the `github-mcp-server` tools to remotely commit these changes. Follow the sequence below:

### Phase 1: Setup Repository Context
1. **Identify Target Repo:** Determine the repository owner and name (e.g., `vinifinger/julius`).
2. **Create Feature Branch:** Use `mcp_github-mcp-server_create_branch` to branch off `master` into a new branch named `feature/github-actions-upgrade`. All subsequent file updates should target this new branch.

### Phase 2: Restructure CI Workflow
1. **Read Existing Workflow:** Use `mcp_github-mcp-server_get_file_contents` to fetch the current `.github/workflows/ci.yml`.
2. **Apply Optimizations:** 
   - Add a `concurrency` block to cancel redundant runs:
     ```yaml
     concurrency:
       group: ${{ github.workflow }}-${{ github.ref }}
       cancel-in-progress: true
     ```
   - Split the mono-job into multiple distinct jobs (`compile`, `test`, `docker`) and use the `needs` parameter to enforce dependency flow (e.g., `docker` needs `test`).
3. **Commit Changes:** Use `mcp_github-mcp-server_create_or_update_file` to commit the rewritten `ci.yml` to the feature branch.

### Phase 3: JaCoCo Code Coverage & Dependencies
1. **Modify Gradle Build:** Use the MCP to read `build.gradle`, inject the `jacoco` plugin, and commit the update.
2. **Add CI Step:** Edit `ci.yml` again (or do it in your initial edit) to include an action (such as `madrapps/jacoco-report`) that publishes coverage metrics to PRs.

### Phase 4: CodeQL & Dependabot
1. **Create CodeQL Workflow:** Use `mcp_github-mcp-server_create_or_update_file` to create a brand new file at `.github/workflows/codeql.yml`. Configure it with the standard `github/codeql-action/init@v3`, `autobuild`, and `analyze` steps.