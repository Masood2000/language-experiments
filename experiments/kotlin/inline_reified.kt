// Experiment: Inline functions + reified type parameters
// Kotlin can preserve generic type information at runtime (unlike Java's type erasure).

inline fun <reified T> isType(value: Any): Boolean {
    return value is T  // This is IMPOSSIBLE in Java due to type erasure!
}

inline fun <reified T> printType() {
    println("  Type: ${T::class.simpleName}")
    println("  Java class: ${T::class.java}")
}

// Inline functions — the function body is copied to the call site
// This enables passing lambdas with zero overhead
inline fun measureAndPrint(label: String, block: () -> Unit) {
    val start = System.nanoTime()
    block()
    val elapsed = (System.nanoTime() - start) / 1_000_000.0
    println("$label took ${elapsed}ms")
}

// noinline — opt specific lambdas out of inlining
inline fun doStuff(inlined: () -> Unit, noinline stored: () -> Unit) {
    inlined()  // Will be inlined at call site
    val ref = stored  // Can store this because it's noinline
    ref()
}

// crossinline — allow lambda in another execution context
inline fun runInThread(crossinline block: () -> Unit) {
    Thread {
        block()  // crossinline allows this, but prevents non-local returns
    }.start()
}

fun main() {
    println("=== Reified types — runtime type checking with generics ===")
    println("isType<String>(\"hello\"): ${isType<String>("hello")}")
    println("isType<String>(42): ${isType<String>(42)}")
    println("isType<Int>(42): ${isType<Int>(42)}")

    println("\n=== Access generic type info at runtime ===")
    print("printType<String>(): ")
    printType<String>()
    print("printType<List<Int>>(): ")
    printType<List<Int>>()

    println("\n=== Inline functions — zero-overhead lambdas ===")
    measureAndPrint("Sum 1M numbers") {
        var sum = 0L
        for (i in 1..1_000_000) sum += i
        println("  Sum = $sum")
    }

    println("\n=== Non-local returns — only possible with inline ===")
    fun findFirst(): String {
        listOf("a", "b", "c").forEach { item ->
            // forEach is inline, so 'return' exits findFirst(), not just the lambda!
            if (item == "b") return "Found $item"
        }
        return "Not found"
    }
    println("findFirst(): ${findFirst()}")

    // With non-inline lambdas, you'd need return@forEach (local return)
    println("\n=== Local return vs non-local return ===")
    listOf(1, 2, 3, 4, 5).forEach {
        if (it == 3) return@forEach  // Local return — skips this iteration only
        print("$it ")
    }
    println("← skipped 3 with return@forEach")
}
