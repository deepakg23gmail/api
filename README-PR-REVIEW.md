# PR Review Agent

A read-only Pull Request review agent for Spring Boot Maven API projects. Reports code quality and design standard findings without modifying, fixing, refactoring, committing, or pushing code.

---

## Table of Contents

1. [Purpose](#purpose)
2. [What the Agent Reviews](#what-the-agent-reviews)
3. [What the Agent Does Not Do](#what-the-agent-does-not-do)
4. [Invocation via OpenCode](#invocation-via-opencode)
5. [Local Usage](#local-usage)
6. [GitHub Actions Integration](#github-actions-integration)
7. [Setting Up OPENCODE_API_KEY](#setting-up-opencode_api_key)
8. [Interpreting Findings](#interpreting-findings)
9. [Branch Protection Setup](#branch-protection-setup)
10. [Demo Guide](#demo-guide)
11. [Troubleshooting](#troubleshooting)

---

## Purpose

The PR Review Agent automates code review for pull requests. It inspects only the files changed in a PR against the `main` branch and produces a structured review report covering code quality, design standards, Spring Boot best practices, test quality, security, performance, and Maven hygiene.

The agent operates under a strict read-only constraint. It never modifies code, creates commits, or pushes changes.

---

## What the Agent Reviews

### Code Quality

- Meaningful class, method, and variable names
- Clear and readable logic
- Small and focused methods
- Long methods, duplicate logic, dead code
- Unused imports, magic numbers
- Excessive nesting, unnecessary complexity
- Inconsistent style, maintainability issues

### Design Quality

- Single responsibility principle
- Separation of concerns
- Low coupling, high cohesion
- Appropriate abstraction level
- Layer boundary violations
- God classes or God services
- Duplicate business rules

### Spring Boot

- Thin controllers with business logic in services
- DTOs for API requests and responses
- Entities not exposed through APIs
- Constructor-based dependency injection
- Correct annotation usage
- Meaningful exception handling
- Clean configuration usage

### REST API

- RESTful endpoint naming
- Correct HTTP methods and status codes
- Versioned API paths
- Consistent request/response structure
- Input validation
- Backward compatibility

### Test Quality

- Unit tests for changed business logic
- Controller tests for changed endpoints
- Meaningful assertions and edge case coverage
- Negative and validation failure tests
- Clear test names
- Avoidance of brittle tests

### Security

- Hardcoded secrets, passwords, or tokens
- Sensitive data in logs
- Missing validation
- Stack traces exposed to clients
- Silent exception swallowing

### Performance

- Unnecessary database calls
- N+1 query risks
- Missing pagination for list APIs
- Repeated expensive operations

### Maven

- Unnecessary or duplicate dependencies
- Incorrect dependency scope
- Hardcoded versions

---

## What the Agent Does Not Do

- Fix code
- Modify source files
- Edit configuration files
- Refactor code
- Rewrite methods or classes
- Generate patches
- Apply changes
- Create commits
- Push changes
- Create pull requests
- Auto-format code
- Auto-resolve findings
- Provide full replacement code

---

## Invocation via OpenCode

### Step 1: Open Terminal

```bash
cd <project-folder>
```

### Step 2: Start OpenCode

```bash
opencode
```

### Step 3: Open the Agent List

Inside OpenCode, type:

```
/agents
```

### Step 4: Select PR Review Agent

Choose **PR Review Agent** from the list.

### Step 5: Run the Review

Type the following prompt:

```
Review the current branch against main. Report only code quality and design standard findings. Do not fix or modify code.
```

### Step 6: Review the Report

The agent will read only the changed files and produce a structured review report in the terminal.

---

## Local Usage

### Basic Review

```bash
cd <project-folder>
opencode
# Inside OpenCode:
/agents
# Select: PR Review Agent
# Prompt: Review the current branch against main. Report only findings. Do not fix code.
```

### Review a Named Branch

```bash
cd <project-folder>
git checkout feature/my-change
opencode
# Inside OpenCode:
/agents
# Select: PR Review Agent
# Prompt: Review the changes in this branch against main. Report only code quality and design standard findings. Do not modify code.
```

### Save the Report to a File

After the agent produces the report in the terminal, you can copy the output to a file:

```bash
# From within OpenCode, ask the agent to review
# Then copy the output manually, or use:
pbpaste > review.md    # macOS
xclip -selection clipboard -o > review.md    # Linux
```

### Review via Git Diff

If you want to review specific commits:

```bash
git log --oneline main..HEAD    # See commits
git diff main...HEAD --stat     # See changed files
```

Then ask the agent:

```
Review the diff between main and HEAD. Focus on the changed files. Report findings only. Do not modify code.
```

---

## GitHub Actions Integration

### How It Works

When a pull request is opened, updated, or reopened against `main`:

1. GitHub Actions starts automatically.
2. The **Build & Test** job compiles the project and runs all tests.
3. The **PR Review** job identifies changed files.
4. The PR Review Agent performs structural analysis and posts findings as a PR comment.
5. If Critical findings are detected, the workflow fails.
6. If no Critical findings exist, the workflow passes.
7. No code is modified at any point.

### Workflow File

The workflow is defined in `.github/workflows/pr-review.yml`.

### Triggers

```yaml
on:
  pull_request:
    branches:
      - main
    types:
      - opened
      - synchronize
      - reopened
```

### Permissions

The workflow uses least-privilege permissions:

```yaml
permissions:
  contents: read
  pull-requests: write
```

- `contents: read` - Read repository files.
- `pull-requests: write` - Post review comments on the PR.

### What the Workflow Does

| Step | Action | Modifies Code? |
|------|--------|----------------|
| Checkout | Clones the repository | No |
| Setup Java | Installs JDK 25 | No |
| Build & Test | Runs `mvn clean verify` | No |
| Identify Changes | Runs `git diff` | No |
| Review Agent | Analyses changed files | No |
| Post Comment | Posts review on PR | No |
| Fail Build | Fails only on Critical findings | No |

---

## Setting Up OPENCODE_API_KEY

The `OPENCODE_API_KEY` secret is used for enhanced AI-powered review analysis. Without it, the workflow performs structural analysis only.

### Steps to Create the Secret

1. Go to your GitHub repository.
2. Navigate to **Settings** > **Secrets and variables** > **Actions**.
3. Click **New repository secret**.
4. Name: `OPENCODE_API_KEY`
5. Value: Your OpenCode API key.
6. Click **Add secret**.

### Without the Secret

The workflow still runs and posts a structural review comment with:

- Changed file statistics
- Build and test results
- Basic structural checks (e.g., controller changes without service changes)
- Security pattern detection

---

## Interpreting Findings

### Severity Levels

| Level | Meaning | Action Required |
|-------|---------|-----------------|
| **Critical** | Must be addressed before merge | Fix immediately |
| **Major** | Should be addressed before merge | Fix before merge |
| **Minor** | Good to address | Fix if convenient |
| **Suggestion** | Optional improvement | Consider for future |

### Critical Examples

- Secret committed to repository
- Broken build risk
- API-breaking change without migration
- Data loss risk

### Major Examples

- Business logic in controller layer
- Missing input validation
- No tests for changed logic
- Weak exception handling

### Minor Examples

- Unclear variable naming
- Minor readability issue
- Small duplication

### Suggestion Examples

- Cleaner alternative approach
- Future enhancement opportunity
- Nice-to-have improvement

### Merge Recommendation

| Recommendation | Meaning |
|---------------|---------|
| **Approved** | No findings or only suggestions |
| **Approved with comments** | Minor findings that do not block merge |
| **Changes requested** | Major findings that should be addressed |
| **Blocked due to critical issues** | Critical findings that must be fixed |

---

## Branch Protection Setup

To make the PR Review Agent a required status check:

1. Go to your GitHub repository **Settings**.
2. Open **Branches** under the Code and automation section.
3. Add or edit a branch protection rule for `main`.
4. Enable the following options:
   - **Require a pull request before merging**
   - **Require status checks to pass before merging**
   - **Require branches to be up to date before merging**
5. In the status check list, search for and select:
   - `Build & Test`
   - `PR Review`
6. Click **Save changes**.

After this configuration, pull requests cannot be merged until both the build and the review pass.

---

## Demo Guide

### Demo Scenario

Introduce a small code quality issue in a feature branch and raise a PR to demonstrate the agent reporting the issue.

### Example Issues to Demonstrate

| Issue | File Type | What to Change |
|-------|-----------|----------------|
| Business logic in controller | Controller.java | Add database query or business rule directly in a controller method |
| Unclear naming | Any Java file | Rename a method to something vague like `doStuff()` |
| Duplicate logic | Any Java file | Copy a block of logic to another method unchanged |
| Missing validation | DTO or Controller | Remove a `@Valid` annotation or validation constraint |
| Weak test assertion | Test file | Replace a meaningful assertion with just `assertNotNull()` |

### Demo Steps

#### Step 1: Create a Feature Branch

```bash
git checkout main
git pull origin main
git checkout -b demo/pr-review-agent
```

#### Step 2: Make a Small Code Quality Issue

Open a controller file and add business logic directly inside a controller method. For example, add a database query or computation that belongs in the service layer.

#### Step 3: Commit the Change

```bash
git add .
git commit -m "Demo: add business logic in controller for PR review demo"
```

#### Step 4: Push the Branch

```bash
git push -u origin demo/pr-review-agent
```

#### Step 5: Raise a Pull Request

1. Go to the GitHub repository.
2. Click **Pull requests** > **New pull request**.
3. Set base: `main`, compare: `demo/pr-review-agent`.
4. Add a title: "Demo: PR Review Agent Test".
5. Click **Create pull request**.

#### Step 6: Wait for GitHub Actions

Monitor the Actions tab. The workflow will:

1. Build and test the project.
2. Run the PR Review Agent.
3. Post a comment on the PR.

#### Step 7: Open the PR Comment

Scroll to the bottom of the pull request conversation tab. The PR Review Agent will have posted a review comment with:

- Changed file statistics
- Findings table with severity, file, area, and recommendation
- Quality checklist

#### Step 8: Confirm the Agent Reports the Issue

The agent should report the business logic in the controller under the Spring Boot review area as a Major finding.

#### Step 9: Confirm the Agent Did Not Modify Code

Check the Files changed tab. There should be only the original commit. The agent did not create any new commits or push any changes.

#### Step 10: Fix the Issue Manually

Move the business logic from the controller to the appropriate service method.

#### Step 11: Commit and Push

```bash
git add .
git commit -m "Address PR review feedback: move business logic to service layer"
git push
```

#### Step 12: Confirm the Workflow Reruns

The `synchronize` event triggers the workflow again. The updated review should show the finding has been resolved.

#### Step 13: Clean Up

After the demo, merge or close the PR and delete the branch:

```bash
git checkout main
git branch -D demo/pr-review-agent
git push origin --delete demo/pr-review-agent
```

---

## Troubleshooting

### Agent Not Visible in /agents

**Cause:** The agent definition file is not in the correct location or OpenCode was not restarted.

**Fix:**
1. Verify the file exists at `.opencode/agents/pr-review-agent.md`.
2. Quit OpenCode and restart it.
3. Run `/agents` again.

### Agent Requesting Write Permissions

**Cause:** The agent permission configuration is incorrect.

**Fix:**
1. Open `.opencode/agents/pr-review-agent.md`.
2. Verify the frontmatter contains:
   ```yaml
   permission:
     read: allow
     edit: deny
     bash: deny
   ```
3. Restart OpenCode.

### GitHub Actions Workflow Not Running

**Cause:** The workflow file is missing or the trigger configuration is incorrect.

**Fix:**
1. Verify `.github/workflows/pr-review.yml` exists.
2. Check that the PR targets the `main` branch.
3. Check the Actions tab for any workflow errors.
4. Verify the workflow file syntax is valid YAML.

### Build Fails in GitHub Actions

**Cause:** The project does not compile or tests fail.

**Fix:**
1. Run `mvn clean verify` locally to reproduce.
2. Fix the build issue in the PR.
3. Push the fix.

### Review Comment Not Posted

**Cause:** The workflow does not have permission to post comments.

**Fix:**
1. Verify `permissions.pull-requests: write` is set in the workflow.
2. Check that the GitHub token has sufficient permissions.
3. Review the Actions logs for errors.

### OPENCODE_API_KEY Not Working

**Cause:** The secret is not configured or has an incorrect value.

**Fix:**
1. Go to repository Settings > Secrets > Actions.
2. Verify `OPENCODE_API_KEY` exists and has the correct value.
3. Re-run the workflow.

### Agent Runs But Produces No Findings

**Cause:** The changed code may have no quality issues, or the changes are in non-Java files only.

**Fix:**
1. This is normal behaviour. The agent reports only actual findings.
2. If you expect findings, review the agent prompt for specificity.

### Workflow Does Not Fail on Critical Findings

**Cause:** The critical finding detection step may not be executing correctly.

**Fix:**
1. Check the Actions logs for the "Check for critical findings" step.
2. Verify the `has_critical` output variable is being set.
3. Review the pattern matching logic in the workflow.

---

## File Reference

| File | Purpose |
|------|---------|
| `.opencode/agents/pr-review-agent.md` | Agent definition for OpenCode |
| `opencode.json` | OpenCode project configuration |
| `.github/workflows/pr-review.yml` | GitHub Actions workflow |
| `README-PR-REVIEW.md` | This documentation file |
| `sample-review-report.md` | Example output from the agent |
