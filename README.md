# Language Experiments

Learn programming languages by experimenting, not by reading books.

Each language has its own folder with standalone experiments that reveal non-obvious, surprising, or unique behaviors — things that only become clear when you actually run the code.

## Languages

- [Python](python/)
- [Kotlin](kotlin/)

## How to Use

Ask Claude Code:

- `"do a new experiment in Python"` — picks an uncovered topic automatically
- `"new experiment in Kotlin on coroutines"` — targets a specific topic
- `"do a new experiment in JavaScript"` — creates a new language folder and starts experimenting

## Running Experiments

```bash
# Python
python3 python/<file>.py

# Kotlin
kotlinc kotlin/<file>.kt -include-runtime -d /tmp/kt_exp.jar && java -jar /tmp/kt_exp.jar
```

## Structure

```
<language>/
├── *.py / *.kt / ...   ← Runnable experiment scripts
└── INSIGHTS.md          ← Findings: What / Expected / Actual / Why
```
