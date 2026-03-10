# Kotlin Experiment Insights

## 1. Null Safety System (`null_safety_tricks.kt`)

**What**: Kotlin eliminates null pointer exceptions at compile time with nullable types (`String?` vs `String`).

**Expected**: You'd think null safety is just syntactic sugar for null checks.

**Actual**: It's deeply integrated:
- `?.` safe calls chain elegantly: `null?.length?.plus(1)` → `null` (no NPE)
- Elvis `?:` provides defaults: `null?.length ?: -1` → `-1`
- Smart casts: after `if (x != null)`, the compiler treats `x` as non-nullable — no `?.` needed
- You can define extension functions on nullable receivers: `null.safeLength()` works
- `Nothing` type (bottom type) is a subtype of every type, used for functions that never return

**Why**: Unlike Java's `@Nullable` annotations (advisory only), Kotlin's null safety is enforced by the type system. The one hole: Java interop returns "platform types" (`String!`) where the compiler can't help.

---

## 2. Equality and Boxing (`equality_and_boxing.kt`)

**What**: `==` (structural) vs `===` (referential) equality behaves differently with nullable types due to JVM boxing.

**Expected**: `Int? = 128` and `Int? = 128` should be identical objects.

**Actual**:
- `Int? = 128 === Int? = 128` → `false` (different boxed `Integer` objects)
- `Int? = 127 === Int? = 127` → `true` (JVM caches `-128..127`)
- Non-nullable `Int` compiles to primitive `int` (no boxing, `===` always true)
- `data class` auto-generates `equals()`/`hashCode()` — regular classes don't
- **Array trap**: `arrayOf(1,2,3) == arrayOf(1,2,3)` → `false`! Must use `contentEquals()`

**Why**: Kotlin's `Int` maps to JVM `int` (primitive) but `Int?` maps to `java.lang.Integer` (boxed object). The JVM caches small Integer instances, creating an identity inconsistency. This is the same as Java but Kotlin makes it more visible because `===` is explicit.

---

## 3. Extension Functions — Static Resolution (`extension_functions.kt`)

**What**: Extensions let you add methods to any class, but they're resolved at compile time, not runtime.

**Expected**: `val animal: Animal = Dog(); animal.sound()` calls `Dog.sound()`.

**Actual**: Calls `Animal.sound()` → `"..."` because extensions are resolved by the **declared type**, not runtime type. Also:
- Member functions always beat extensions with the same signature
- You can define extensions on `null` (`String?.safeLength()` works when called on null)
- Extension properties exist but can't store state (no backing field)
- Scope functions (`let`, `run`, `apply`, `also`, `with`) are all built on extensions + lambdas

**Why**: Extensions are compiled to static methods. `fun Animal.sound()` becomes `static sound(Animal)`. The JVM dispatches static methods by the declared parameter type, not the runtime type. This is fundamentally different from virtual method dispatch.

---

## 4. Delegation — `by` Keyword (`delegation_magic.kt`)

**What**: Kotlin's `by` keyword enables property delegation and interface delegation with minimal code.

**Key findings**:
- `lazy { }` computes once on first access (thread-safe by default)
- `Delegates.observable` fires a callback on every change
- `Delegates.vetoable` can **reject** invalid values — setting `-5` to a positive-only property silently keeps the old value
- Properties can delegate to a `Map` — `val name: String by map` reads key `"name"` from the map
- Interface delegation: `class A(b: B) : Interface by b` forwards all interface methods to `b` with zero boilerplate

**Why**: Delegation is Kotlin's answer to the "composition over inheritance" principle. Instead of inheriting behavior, you delegate to a contained object. The compiler generates all the forwarding methods. Custom delegates use `operator fun getValue/setValue` — a convention-based protocol similar to Python's descriptors.

---

## 5. Sealed Classes + When Expressions (`sealed_and_when.kt`)

**What**: Sealed classes restrict inheritance to a known set. `when` expressions can exhaustively match them.

**Expected**: Like enums but limited to constants.

**Actual**: Much more powerful:
- Each subclass can have different properties (`Success` has `data`, `Error` has `code` + `message`)
- `when` is an **expression** (returns a value), not just a statement
- The compiler **errors** if you don't handle all sealed subclasses (no `else` needed)
- Smart casting inside `when`: `is NetworkResult.Success` automatically casts to `Success`
- Works with `sealed interface` (Kotlin 1.5+) for multiple inheritance

**Why**: Sealed classes model "sum types" / "discriminated unions" from functional programming. Combined with `when`, they give you exhaustive pattern matching — the compiler guarantees you handle every case. Adding a new subclass breaks all incomplete `when` expressions at compile time, preventing missed branches.

---

## 6. Inline Functions + Reified Types (`inline_reified.kt`)

**What**: `inline` copies function body to call site. `reified` preserves generic type info at runtime.

**Expected**: Generics are erased at runtime (like Java).

**Actual**:
- `inline fun <reified T> isType(value: Any) = value is T` — **impossible in Java** due to erasure
- `T::class` is accessible at runtime with reified
- Inline lambdas have zero allocation overhead (no anonymous class created)
- **Non-local returns**: `return` inside an inline lambda exits the enclosing function, not just the lambda
- `return@forEach` is a local return (skips current iteration only)

**Why**: `inline` literally copies the function body into the call site at compile time. Since the concrete type is known at each call site, `reified` substitutes it in. This is why reified only works with inline — without inlining, there's no call site to substitute types into.

---

## 7. Collections vs Sequences (`collection_sequences.kt`)

**What**: Collection operations (map/filter) are eager. Sequence operations are lazy.

**Expected**: Both produce the same result, so performance should be similar.

**Actual** (finding first square > 20 from 1..10):
- **Eager**: `map(1) map(2)...map(10)` then `filter(1) filter(4)...filter(100)` — 20 operations
- **Lazy**: `map(1) filter(1) map(2) filter(4) ... map(5) filter(25)` — **10 operations**, stops early

Also:
- Sequences support **infinite** generation: `generateSequence` for Fibonacci works because items are computed on demand
- Operation order matters: filter-first needed 10 filters + 5 maps; map-first needed 10 of each

**Why**: Collections create intermediate lists at each step. Sequences process one element through the entire chain before moving to the next. This enables short-circuiting (`first`, `take`) and infinite sequences. Similar to Java Streams but with a simpler API.

---

## 8. Coroutines vs Threads (`coroutine_vs_thread.kt`)

**What**: Coroutines are lightweight concurrent primitives — not threads.

**Key insights** (requires kotlinx-coroutines library):
- 100,000 coroutines launch in ~1 second. 100,000 threads would need ~100GB stack memory
- Coroutines are **cooperative**: they suspend at explicit points (`delay`, `yield`). Threads are preemptive
- **Structured concurrency**: parent automatically waits for children; cancelling parent cancels all children
- `async`/`await` for concurrent tasks that return values
- Dispatchers control the thread pool: `Default` (CPU), `IO` (blocking), `Main` (UI)

**Why**: Each thread needs ~1MB stack allocated by the OS. Coroutines are just objects on the heap (~few hundred bytes). Suspension is a compiler transformation (state machine), not an OS context switch. This makes them orders of magnitude cheaper than threads for concurrent I/O-heavy workloads.

---

## 9. Nothing Type Tricks (`nothing_type_tricks.kt`)

**What**: Demonstrates Kotlin's Nothing type -- a bottom type that is a subtype of every other type, making throw, TODO(), and emptyList() work in any expression context.

**Expected**: throw is a statement (like Java), and empty collections need explicit type parameters.

**Actual**: throw is an expression of type Nothing, which is a subtype of every type. This means `val x: String = nullableVal ?: throw Exception()` compiles because Nothing <: String. TODO() returns Nothing, so it satisfies any return type. emptyList() returns List<Nothing>, compatible with List<String>, List<Int>, etc. Nothing has zero instances; Nothing? has exactly one value: null. Infinite loops (while(true)) have type Nothing.

**Why**: Nothing is Kotlin's bottom type (the dual of Any, the top type). In type theory, the bottom type is a subtype of all types. Since Nothing has no instances, it can only appear in expressions that never complete normally (throw, infinite loops, process exit). This is more principled than Java's approach where throw is a statement and void is not a real type.

---

## 10. Companion Object Surprises (`companion_object_surprises.kt`)

**What**: Demonstrates that Kotlin companion objects are real singleton objects that can implement interfaces, maintain state, and have their own initialization lifecycle.

**Expected**: Companion objects are just Kotlin's syntax for Java static members.

**Actual**: Companion objects can implement interfaces -- Widget's companion implements Factory<Widget> and can be passed directly as a Factory argument. They have their own init blocks that run once (on first access), before any instance init. They are real singleton objects with identity (Widget.Companion). Named companions (companion object Config) provide clearer access patterns. Mutable state in companions is shared across all instances, persisting like static fields.

**Why**: Unlike Java's static members (which belong to the class, not an object), Kotlin companion objects are actual singleton instances of a generated class (e.g., Widget$Companion). This means they participate in the object system -- they can implement interfaces, be passed as arguments, and have their own initialization. On the JVM, @JvmStatic generates actual static methods for Java interop.
