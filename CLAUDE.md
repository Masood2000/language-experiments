# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Purpose

This is a language experimentation project. The goal is to explore programming languages through hands-on experiments that reveal non-obvious, unique, or surprising behaviors — things you don't learn from textbooks.

## How It Works

1. Check `language.txt` for the current target language.
2. Create experiments in `<language>/` — each experiment is a standalone script.
3. Each experiment should demonstrate something surprising, non-obvious, or unique to that language.
4. Write findings to `<language>/INSIGHTS.md` after running experiments.

## Rules for Experiments

- **No web apps, no frameworks** — pure language experiments only.
- Each experiment file should be self-contained and runnable independently.
- Name experiments descriptively: `floating_point_trap.py`, `mutable_default_gotcha.py`, etc.
- Every experiment must include comments explaining what's happening and why it's surprising.
- Focus on behaviors that differ from other languages or defy common assumptions.
- Run the experiments and capture actual output in INSIGHTS.md.

## Running Experiments

- Python: `python3 python/<file>.py`
- Kotlin: `kotlinc kotlin/<file>.kt -include-runtime -d /tmp/kt_exp.jar && java -jar /tmp/kt_exp.jar`
- JavaScript: `node javascript/<file>.js`
- Other languages: use standard toolchain for that language.

## Kotlin-specific Notes

- Sealed classes, interfaces, data classes, and custom delegates must be top-level (not inside `main()`).
- Coroutine experiments require `kotlinx-coroutines-core` on the classpath.
- Nullable types (`Int?`) box to `java.lang.Integer` — this affects `===` identity checks.

## INSIGHTS.md Format

Each insight should have:
- **What**: the experiment (brief code snippet or reference to file)
- **Expected**: what most people would assume
- **Actual**: what actually happens
- **Why**: the language internals that cause this behavior
