<p align="center">
  <img src="banner.svg" alt="Language Experiments" />
</p>

Learn programming languages by experimenting.

Each language has its own folder with standalone experiments that reveal non-obvious, surprising, or unique behaviors — things that only become clear when you actually run the code.

Better the model, better the experiments you will see and learn from.

## Languages

- [C](c/)
- [C++](cpp/)
- [Go](go/)
- [Java](java/)
- [JavaScript](javascript/)
- [Kotlin](kotlin/)
- [Python](python/)
- [Ruby](ruby/)
- [Rust](rust/)
- [Swift](swift/)

## Adding New Experiments

Open this project in [Claude Code](https://claude.ai/code) and ask:

- `"do a new experiment in Python"` — picks an uncovered topic automatically
- `"new experiment in Kotlin on coroutines"` — targets a specific topic
- `"do a new experiment in Dart"` — creates a new language folder and starts experimenting

Claude Code reads existing experiments first and only creates new, non-overlapping ones. Each new experiment is run, verified, and its insights are appended to `INSIGHTS.md`.

## Structure

Each language lives in its own top-level folder:

```
language-experiments/
├── python/
│   ├── mutable_default_gotcha.py    <- Self-contained experiment script
│   ├── closure_late_binding.py
│   ├── ...
│   └── INSIGHTS.md                  <- All findings for Python
├── kotlin/
│   ├── equality_and_boxing.kt
│   ├── ...
│   └── INSIGHTS.md
├── javascript/
│   ├── type_coercion_madness.js
│   ├── ...
│   └── INSIGHTS.md
└── ... (c, cpp, go, java, ruby, rust, swift)
```

- **Experiment files** — Each file is standalone and runnable. Named descriptively after the behavior it demonstrates.
- **INSIGHTS.md** — Documents every experiment's findings in a structured format: What happened, what was expected, what actually happened, and why.

## Running Experiments

### Using Claude Code

Ask Claude Code to run and verify experiments for you.

### Manually

```bash
# Python
python3 python/<file>.py

# JavaScript
node javascript/<file>.js

# Kotlin
kotlinc kotlin/<file>.kt -include-runtime -d /tmp/kt_exp.jar && java -jar /tmp/kt_exp.jar

# Java
javac java/<file>.java -d /tmp/java_exp && java -cp /tmp/java_exp <ClassName>

# C
gcc -std=c17 -o /tmp/c_exp c/<file>.c && /tmp/c_exp

# C++
g++ -std=c++17 -o /tmp/cpp_exp cpp/<file>.cpp && /tmp/cpp_exp

# Go
go run go/<file>.go

# Rust
rustc rust/<file>.rs -o /tmp/rust_exp && /tmp/rust_exp

# Ruby
ruby ruby/<file>.rb

# Swift
swiftc swift/<file>.swift -o /tmp/swift_exp && /tmp/swift_exp
```
