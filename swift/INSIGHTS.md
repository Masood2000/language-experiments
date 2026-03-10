# Swift Experiment Insights

## 1. Value vs Reference Types (`value_vs_reference.swift`)

**What**: Demonstrates struct copy semantics, class reference sharing, array copy-on-write, and the shallow copy trap when a struct contains a class reference.

**Expected**: Copying a struct gives a fully independent copy. If a struct "contains" another object, that object is also independently copied.

**Actual**: Structs are copied by value, but any class-typed properties inside the struct still share the same reference. Mutating `car2.engine.horsepower` also changes `car1.engine.horsepower` because both structs point to the same `Engine` object.

**Why**: Swift value types perform a shallow copy. Class references inside structs are just pointers that get duplicated, not the objects they point to. Arrays avoid unnecessary copying through copy-on-write (CoW): the underlying buffer is shared until one copy is mutated, at which point the buffer is duplicated. Custom types do not get CoW for free.

---

## 2. Optional Chaining (`optional_chaining.swift`)

**What**: Demonstrates nested optionals (`Optional<Optional<Int>>`), optional chaining, and `.map` vs `.flatMap` on optionals.

**Expected**: Looking up a key in a `[String: Int?]` dictionary returns `Int?`. Nil means the key doesn't exist.

**Actual**: The return type is `Int??` (double-wrapped optional). A key that exists with a `nil` value returns `Optional(nil)`, while a missing key returns `nil`. Both look nil-ish but behave differently with `if let`.

**Why**: Dictionary subscript returns `Optional<Value>`. When `Value` is itself `Int?` (i.e., `Optional<Int>`), the return type becomes `Optional<Optional<Int>>`. The outer layer indicates key presence; the inner layer holds the value. Similarly, `.map` on an optional that transforms to another optional produces a double-wrapped result, while `.flatMap` automatically flattens it.

---

## 3. Protocol Extensions (`protocol_extensions.swift`)

**What**: Demonstrates default implementations in protocol extensions and the critical difference between static and dynamic dispatch.

**Expected**: If a type provides its own implementation of a method defined in a protocol extension, that override is always used regardless of how the value is referenced.

**Actual**: Methods declared only in the extension (not in the protocol requirements) use **static dispatch**. When a `Bird` is stored as a `Describable` variable, calling `staticMethod()` invokes the extension's default implementation, completely ignoring `Bird`'s override.

**Why**: The Swift compiler uses a witness table (like a vtable) for methods listed in the protocol declaration, enabling dynamic dispatch. Methods added only in the extension are resolved at compile time based on the static type of the variable. The compiler has no entry in the witness table for these methods, so it falls back to the extension's implementation.

---

## 4. Enum Power (`enum_power.swift`)

**What**: Demonstrates associated values, raw values, recursive (indirect) enums for building expression trees, and advanced pattern matching.

**Expected**: Enums are simple named integer constants (as in C/C++).

**Actual**: Swift enums are full algebraic data types. Each case can carry different associated data. The `indirect` keyword enables recursive definitions, allowing you to build tree structures like arithmetic expression evaluators. Pattern matching with `if case`, `for case`, and `where` clauses provides powerful destructuring.

**Why**: Swift enums are implemented as tagged unions (discriminated unions). The `indirect` keyword tells the compiler to heap-allocate the recursive payload to break the infinite-size problem. `CaseIterable` auto-generates an `allCases` array. Combined with pattern matching, Swift enums replace entire class hierarchies used in other languages.

---

## 5. Closure Capture (`closure_capture.swift`)

**What**: Demonstrates capture-by-reference vs capture-by-value (capture lists), the for-in vs while loop gotcha, reference cycles with closures, and `@autoclosure`.

**Expected**: Closures capture variables the same way regardless of loop construct. Storing a closure in a `lazy` property is safe.

**Actual**: `for-in` loops create a new binding per iteration (each closure captures its own copy), but `while` loops share a single variable (all closures see the final value). A `lazy var` closure that references `self` creates a retain cycle because the object owns the closure and the closure strongly captures the object.

**Why**: Swift's `for-in` creates a fresh `let` constant for each iteration, while `while` loops use a single mutable variable. Closures capture variables by reference by default. Capture lists (`[value]`, `[weak self]`, `[unowned self]`) override this. `@autoclosure` wraps an expression in a closure, deferring evaluation until the closure is called, which enables short-circuit semantics in custom functions.

---

## 6. Access Control (`access_control.swift`)

**What**: Demonstrates `private`, `fileprivate`, `private(set)`, and the surprising rule that same-file extensions can access `private` members.

**Expected**: `private` means only the type's own `{ }` block can access the member. Extensions cannot.

**Actual**: Since Swift 4, `private` members are accessible from extensions of the same type **in the same file**. This makes `private` and `fileprivate` nearly identical in single-file code. Also, a struct with any `private` stored property loses its auto-generated public memberwise initializer.

**Why**: The Swift team relaxed `private` in SE-0169 to include same-file extensions, recognizing that splitting a type across extensions in the same file is a common organizational pattern. The memberwise initializer rule exists because exposing a way to set a `private` property from outside would violate access control.

---

## 7. Generics and Constraints (`generics_constraints.swift`)

**What**: Demonstrates `where` clauses, associated types, `AnyHashable` type erasure, conditional conformance, and a subtle overload resolution surprise.

**Expected**: If there's a specialized `describe(_ value: Int)` overload and a generic `describe<T>(_ value: T)`, calling `describe(42)` from inside another generic function will use the specialized version.

**Actual**: Inside `callDescribe<T>(_ value: T)`, calling `describe(value)` always dispatches to the generic overload, even when `T` is `Int`. The `Int`-specific overload is never considered because overload resolution happens at compile time based on the static type `T`, not the runtime type.

**Why**: Swift resolves function overloads at compile time. Inside a generic context, the compiler only knows the constraints on `T`, not its concrete type. The specialized `describe(_ value: Int)` is only chosen when the compiler statically knows the argument is `Int`. Additionally, `AnyHashable(1) != AnyHashable(1.0)` because type erasure preserves the underlying type for equality checks.

---

## 8. String and Unicode (`string_unicode.swift`)

**What**: Demonstrates grapheme clusters, the difference between `.count`, `.utf8.count`, `.utf16.count`, and `.unicodeScalars.count`, canonical equivalence, and extreme cases with combining marks.

**Expected**: The family emoji `👨‍👩‍👧‍👦` has a length greater than 1. The precomposed `"é"` (U+00E9) and decomposed `"e" + combining accent` (U+0301) are different strings.

**Actual**: `👨‍👩‍👧‍👦` has `.count` of 1 despite being composed of 7 Unicode scalars and 25 UTF-8 bytes. The precomposed and decomposed forms of `"é"` are `==` equal. A base character with 8 stacked combining marks still counts as 1 `Character`.

**Why**: Swift's `String` operates at the grapheme cluster level per the Unicode standard. A `Character` in Swift is an **extended grapheme cluster**, not a code point or byte. String comparison uses Unicode canonical equivalence (NFC/NFD normalization), so different byte representations of the same visual character are equal. This makes `.count` O(n) since the runtime must walk the string to find cluster boundaries, which is why Swift forbids integer subscripting on strings.

---

## 9. Mutating & inout Surprises (`mutating_inout_surprises.swift`)

**What**: Demonstrates how inout uses copy-in/copy-out semantics (not pass-by-reference) and how mutating methods can completely replace self.

**Expected**: inout parameters work like pass-by-reference (C++ &), and mutating just allows field modification.

**Actual**: inout uses copy-in/copy-out, which triggers willSet/didSet property observers even when passing to a function (the value is copied back). inout works with computed properties (getter called, value modified, setter called). Swift enforces exclusive access -- you can't pass the same variable as two inout parameters. mutating methods on value types can completely replace self (self = newValue). let makes value types truly immutable -- can't call mutating methods. Closures cannot capture inout parameters.

**Why**: Copy-in/copy-out was chosen over pass-by-reference for safety. It ensures property observers always fire and prevents aliasing bugs. Swift's exclusive access rule (SE-0176) prevents simultaneous reads and writes to the same memory. mutating is syntactic sugar for an implicit inout self parameter, which is why it can replace self entirely. Closures can't capture inout because the parameter's lifetime may end before the closure executes.

---

## 10. Type Casting & Any Traps (`type_casting_any_traps.swift`)

**What**: Demonstrates how Any silently wraps optionals, numeric casting through Any fails, and T.self vs type(of:) diverge.

**Expected**: Any works like a transparent container, and type casting through Any behaves like direct casting.

**Actual**: Assigning an Optional to Any wraps the Optional inside Any -- type(of:) shows Optional<String>. Numeric casts that normally work (Int to Double) FAIL through Any: `(42 as Any) as? Double` returns nil. T.self in a generic function shows the declared type (Any), while type(of:) shows the actual runtime type (Int). Protocol conformance checks work correctly through Any. AnyObject only holds class instances; value types require Any.

**Why**: Any is Swift's top type but uses existential containers that preserve the exact original type. Unlike implicit numeric conversions, as? only performs same-type or subtype casts. T.self is resolved at compile time based on the static type parameter, while type(of:) is a runtime introspection. The distinction between Any and AnyObject reflects Swift's value-type-first design.
