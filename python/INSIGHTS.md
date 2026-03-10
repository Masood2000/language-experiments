# Python Experiment Insights

## 1. Integer & String Identity Caching (`identity_caching.py`)

**What**: Python pre-caches integer objects for -5 to 256 and interns certain strings. Using `is` instead of `==` can give surprising results.

**Expected**: Two variables with the same value are always different objects.

**Actual**: `256 is 256` → `True` (cached), but `257 is 257` → may be `True` or `False` depending on context. In a script, the compiler may optimize constants into the same object even beyond the cache range. In the REPL, each line is compiled separately so 257 creates separate objects.

**Why**: CPython pre-allocates small integers at startup for performance. Strings that look like valid identifiers are "interned" (deduplicated). This is an implementation detail — never rely on `is` for value comparison.

---

## 2. Mutable Default Arguments (`mutable_default_gotcha.py`)

**What**: Default arguments like `def f(x=[])` share the same list across all calls.

**Expected**: Each call gets a fresh empty list.

**Actual**: The list accumulates: `['a']` → `['a', 'b']` → `['a', 'b', 'c']`.

**Why**: Default values are evaluated **once** at function definition time, not per call. The default object is stored on `f.__defaults__` and reused. Fix: use `None` as sentinel and create the list inside the function body.

---

## 3. Late Binding Closures (`closure_late_binding.py`)

**What**: Lambdas/closures created in a loop all return the same value.

**Expected**: `[lambda: i for i in range(5)]` gives functions returning 0, 1, 2, 3, 4.

**Actual**: All five functions return `4`.

**Why**: Python closures capture **variables**, not **values**. The variable `i` is looked up at call time, and by then the loop has finished with `i == 4`. Fix: use `lambda i=i: i` to capture the current value as a default argument.

---

## 4. `is` vs `==` and NaN (`is_vs_equals.py`)

**What**: `is` checks object identity, `==` checks value equality. These diverge in surprising ways.

**Key findings**:
- `True == 1` is `True` and `isinstance(True, int)` is `True` — booleans are literally integers in Python. `True + True + True == 3`.
- `float('nan') == float('nan')` is `False` — NaN is not equal to itself (IEEE 754 standard). But `nan is nan` is `True` if it's the same object.
- `None is None` is always `True` — None is a singleton. That's why the idiom is `x is None` not `x == None`.

**Why**: `bool` is a subclass of `int` in Python (`True=1`, `False=0`). NaN's self-inequality is per the IEEE 754 floating point spec, which Python follows.

---

## 5. List Multiplication Trap (`list_multiplication_trap.py`)

**What**: `[[0]*3]*3` creates a grid where all rows are the **same object**.

**Expected**: Modifying `grid[0][0]` only changes the first row.

**Actual**: All three rows change because they're references to the same list. `id(grid[0]) == id(grid[1]) == id(grid[2])` → `True`.

**Why**: `*` on a list copies references, not objects. `[obj] * 3` gives `[obj, obj, obj]` — three references to the same `obj`. Fix: use `[[0]*3 for _ in range(3)]` to create independent lists. Note: `[0]*3` is fine because integers are immutable.

---

## 6. Walrus Operator & Advanced Unpacking (`walrus_and_unpacking.py`)

**What**: Python has unique syntax features not found in most languages.

**Key findings**:
- Extended unpacking: `first, *middle, last = [1,2,3,4,5]` → `middle = [2,3,4]`. The `*` variable catches all remaining items.
- Walrus operator `:=`: Assign and use in one expression. `if (n := len(data)) > 5:` both assigns and tests.
- Chained comparisons: `1 < x < 10` works as mathematical notation. Translates to `(1 < x) and (x < 10)` without evaluating `x` twice.

**Why**: These are deliberate language design choices. Chained comparisons are nearly unique to Python. The walrus operator (PEP 572, Python 3.8) was controversial but enables cleaner patterns especially in `while` loops and comprehensions.

---

## 7. Descriptor Protocol (`descriptor_magic.py`)

**What**: `@property`, `@classmethod`, and even regular methods all work through Python's descriptor protocol.

**Key findings**:
- A descriptor is any object with `__get__`, `__set__`, or `__delete__` methods.
- Functions are descriptors! `MyClass.__dict__['greet']` is a function, but `obj.greet` is a **bound method** — the function's `__get__` automatically binds `self`.
- `__set_name__` (Python 3.6+) lets descriptors know their own attribute name automatically.

**Why**: The descriptor protocol is one of Python's most powerful and least-known features. It's the mechanism behind attribute access, making `@property`, `@staticmethod`, `@classmethod`, and `__slots__` all possible. Understanding descriptors means understanding how Python's object model actually works.

---

## 8. Garbage Collection & Reference Cycles (`gc_reference_cycles.py`)

**What**: Python uses reference counting as primary GC, with a cyclic collector for reference cycles.

**Key findings**:
- `sys.getrefcount(a)` shows the live reference count (always +1 because the function call itself holds a reference).
- Circular references (A→B→A) survive `del` because refcount never reaches 0. With cyclic GC disabled, these objects leak.
- `gc.collect()` found and freed 135 objects including our circular nodes.
- Python GC has 3 generations with configurable thresholds `(2000, 10, 0)`. Young objects are collected frequently, old objects rarely.
- `weakref.ref()` creates references that don't increase refcount — useful for caches and breaking cycles.

**Why**: Unlike Java/Go which use tracing GC only, Python's hybrid approach (refcount + cyclic GC) gives deterministic cleanup for most objects (immediate `__del__` when refcount hits 0) while still handling cycles. The generational system is based on the hypothesis that most objects die young.

---

## 9. Tuple Hashability Surprise (`tuple_hashability_surprise.py`)

**What**: Demonstrates that tuples are NOT always hashable despite being "immutable", and the famous `+=` paradox where an operation both succeeds AND raises an error.

**Expected**: Tuples are immutable, so they should always be hashable and their contents should never change.

**Actual**: A tuple containing a list (e.g., `(1, 2, [3, 4])`) raises `TypeError: unhashable type: 'list'` when hashed. You can mutate lists inside tuples via `append()`. Most surprisingly, `t[0] += [5, 6]` on a tuple containing a list both raises `TypeError` AND mutates the list — the list is extended, but the tuple assignment fails.

**Why**: Tuples are immutable in that their references can't be reassigned, but the objects they reference can still be mutable. Hashability requires all elements to be hashable (recursive check). The `+=` paradox occurs because `t[0] += [5, 6]` compiles to: (1) `temp = t[0].__iadd__([5, 6])` which succeeds and mutates the list in-place, then (2) `t[0] = temp` which fails because tuples don't support item assignment. The mutation happens before the assignment error.

---

## 10. Exception Chaining Trap (`exception_chaining_trap.py`)

**What**: Explores Python's exception chaining (`__context__`, `__cause__`, `from None`) and how `return` in `finally` silently swallows exceptions.

**Expected**: Exceptions are straightforward — one exception at a time, and `finally` is just for cleanup.

**Actual**: When an exception is raised while handling another, Python implicitly chains them via `__context__`. Using `raise X from Y` sets `__cause__` for explicit chaining. `from None` suppresses the chain in tracebacks (but `__context__` is still set). Most dangerously, a `return` in `finally` silently swallows any exception from `try` — no traceback, no error, the exception simply vanishes. It also overrides any `return` in `try`.

**Why**: PEP 3134 introduced exception chaining in Python 3. `__context__` is set automatically when raising during exception handling. `__cause__` is set by the `from` clause. `__suppress_context__` controls traceback display. The `return`-in-`finally` behavior follows from the language spec: `finally` always executes, and its control flow (return/break/continue) takes precedence over any pending exception or return value from `try`/`except`.
