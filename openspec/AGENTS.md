# OpenSpec Agent Instructions

## Overview

This project uses OpenSpec for managing change proposals. OpenSpec provides a structured approach to planning, documenting, and implementing changes.

## Directory Structure

```
openspec/
├── AGENTS.md          # This file - instructions for AI agents
├── project.md         # Project context and guidelines
├── specs/             # Approved specifications (capabilities)
└── changes/           # Change proposals
    └── <change-id>/
        ├── proposal.md    # Change summary and rationale
        ├── tasks.md       # Implementation task list
        ├── design.md      # Architectural design (when needed)
        └── specs/         # Spec deltas for this change
            └── <capability>/
                └── spec.md
```

## Proposal Workflow

1. **Scaffold**: Create `proposal.md`, `tasks.md`, and optionally `design.md`
2. **Spec Deltas**: Draft changes to specifications in `specs/<capability>/spec.md`
3. **Validate**: Ensure all files are complete and consistent
4. **Review**: Get user approval before implementation
5. **Apply**: Implement the approved changes
6. **Archive**: Move completed changes to archive

## Spec Delta Format

Use these section headers in spec.md files:

```markdown
## ADDED Requirements

### Requirement: <REQ-ID>
<description>

#### Scenario: <scenario-name>
Given <context>
When <action>
Then <expected-outcome>

## MODIFIED Requirements

### Requirement: <REQ-ID>
<original> -> <modified>

## REMOVED Requirements

### Requirement: <REQ-ID>
<reason for removal>
```

## Change ID Conventions

Use verb-led identifiers:
- `add-feature-name`
- `extend-component-capability`
- `refactor-module-structure`
- `fix-issue-description`

## Best Practices

1. Keep changes focused and minimal
2. Include at least one scenario per requirement
3. Cross-reference related capabilities
4. Consider backward compatibility
5. Document breaking changes explicitly
