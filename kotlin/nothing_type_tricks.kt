/**
 * nothing_type_tricks.kt
 *
 * Demonstrates Kotlin's Nothing type -- a bottom type that is a subtype
 * of every other type. This is why throw, TODO(), and emptyList()
 * work in any expression context.
 */

fun main() {
    println("=== Nothing Type Tricks ===\n")

    // Experiment 1: throw is an expression of type Nothing
    println("1. throw is an expression of type Nothing:")
    val message: String = try {
        throw IllegalStateException("error")
    } catch (e: Exception) {
        "caught: ${e.message}"
    }
    println("   val message: String = try { throw ... } catch { ... }")
    println("   message = $message")
    println("   throw has type Nothing, which is a subtype of String!")

    println()

    // Experiment 2: Elvis operator with throw
    println("2. Elvis operator with throw:")
    val nullableValue: String? = null
    val result: String = nullableValue ?: run {
        println("   null detected, using throw in elvis")
        "default value"  // Using default instead of throw for demo
    }
    println("   val x: String = nullable ?: throw Exception()")
    println("   Nothing (from throw) is subtype of String, so types match!")

    println()

    // Experiment 3: TODO() returns Nothing
    println("3. TODO() returns Nothing (compiles but crashes at runtime):")
    fun futureFeature(): Int {
        // TODO() has return type Nothing, which satisfies Int return
        // Uncomment to see: TODO("implement me")
        return 42  // Using this instead to avoid crash
    }
    println("   fun futureFeature(): Int = TODO()")
    println("   Compiles fine because Nothing is subtype of Int!")
    println("   futureFeature() = ${futureFeature()}")

    println()

    // Experiment 4: emptyList() and Nothing
    println("4. emptyList<Nothing>() is compatible with any List type:")
    val strings: List<String> = emptyList()  // Actually List<Nothing>
    val ints: List<Int> = emptyList()
    val things: List<Any> = emptyList()
    println("   val strings: List<String> = emptyList()  // works!")
    println("   val ints: List<Int> = emptyList()         // works!")
    println("   val things: List<Any> = emptyList()       // works!")
    println("   emptyList() returns List<Nothing>, compatible with all!")

    println()

    // Experiment 5: Nothing vs Nothing? vs Unit
    println("5. Nothing vs Nothing? vs Unit:")
    println("   Nothing  -- no instances exist, for expressions that never return")
    println("   Nothing? -- only value is null (the type of 'null' itself)")
    println("   Unit     -- exactly one value (like void but is a real object)")

    val n: Nothing? = null  // Only possible value
    println("   val n: Nothing? = null  // OK, only possible value")
    println("   n = $n")

    val u: Unit = Unit
    println("   val u: Unit = $u")
    println("   Unit is a singleton object, Nothing has NO instances")

    println()

    // Experiment 6: Nothing in when expressions
    println("6. Nothing makes exhaustive when easier:")
    println("   sealed class Result { Success, Error }")
    println("   fun handle(r: Result): String = when (r) {")
    println("       is Success -> r.value")
    println("       is Error -> throw Exception(r.msg)  // Nothing <: String")
    println("   }")
    println("   throw branch returns Nothing, which is subtype of String!")
    println("   Both branches are String-compatible")

    println()

    // Experiment 7: Infinite loops have type Nothing
    println("7. Infinite functions return Nothing:")
    println("   fun infinite(): Nothing { while(true) { } }")
    println("   The compiler knows this function never returns")
    println("   Any code after calling it is flagged as unreachable")
    println("   val x: String = infinite() // compiles! (never actually assigns)")
}
