---
name: cartographer
description: Maps and navigates repository architecture using Cartographer v2 before implementing changes.
---

# Cartographer for Codex

Source:
https://github.com/kingbootoshi/cartographer

Local repository:
C:\Users\Lucas Theiss\.codex-tools\cartographer

Project:
C:\Users\Lucas Theiss\Documents\Projetos\faculdade

Architecture map:
C:\Users\Lucas Theiss\Documents\Projetos\faculdade\.cartographer\CODEBASE_MAP.md

## Mandatory workflow

1. Read CODEBASE_MAP.md before modifying architecture.
2. Inspect the real repository to validate the map.
3. Identify models, repositories, services and controllers.
4. Trace HTTP routes and Thymeleaf views.
5. Trace database migrations and SQL operations.
6. Identify component dependencies and boundaries.
7. Generate targeted Cartographer briefs when useful.
8. Reindex after substantial architectural changes.
9. Verify that generated artifacts are current.

## Available CLI

Run from the Cartographer repository.

Index:
bun run cartographer:index -- --root "C:\Users\Lucas Theiss\Documents\Projetos\faculdade" --out "C:\Users\Lucas Theiss\Documents\Projetos\faculdade\.cartographer"

Verify:
bun run cartographer:verify -- --root "C:\Users\Lucas Theiss\Documents\Projetos\faculdade" --out "C:\Users\Lucas Theiss\Documents\Projetos\faculdade\.cartographer" --fresh

View:
bun run cartographer:view -- --out "C:\Users\Lucas Theiss\Documents\Projetos\faculdade\.cartographer"

Brief:
bun run cartographer:brief -- --out "C:\Users\Lucas Theiss\Documents\Projetos\faculdade\.cartographer" --path src/main/java --mode implementation --json

Inspect CLI help if a command requires different options.

## Important

Use the standalone Cartographer v2 CLI.

The original Claude Code plugin is a separate workflow.
Do not assume its Claude-specific agents work in Codex.

Do not expose secrets, passwords or .env contents.

The graph supplements code inspection and testing.