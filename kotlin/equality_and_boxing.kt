// Experiment: Kotlin's equality (== vs ===) and how nullable types cause boxing
// Kotlin compiles to JVM bytecode, and primitive boxing creates identity surprises.

fun main() {
    println("=== == is structural equality (like Java's .equals()) ===")
    println("=== === is referential equality (like Java's ==) ===")

    val a: Int = 127
    val b: Int = 127
    println("a = 127, b = 127")
    println("a == b: ${a == b}")    // true — same value
    println("a === b: ${a === b}")  // true — JVM caches -128 to 127

    println()
    val c: Int = 128
    val d: Int = 128
    println("c = 128, d = 128")
    println("c == d: ${c == d}")    // true — same value
    println("c === d: ${c === d}")  // true — Kotlin optimizes non-nullable Int to primitive

    println("\n=== Nullable types cause boxing! ===")
    val e: Int? = 128
    val f: Int? = 128
    println("e: Int? = 128, f: Int? = 128")
    println("e == f: ${e == f}")    // true — value equality
    println("e === f: ${e === f}")  // FALSE — different boxed Integer objects!

    val g: Int? = 127
    val h: Int? = 127
    println("\ng: Int? = 127, h: Int? = 127")
    println("g == h: ${g == h}")    // true
    println("g === h: ${g === h}")  // true — JVM Integer cache for -128..127

    println("\n=== data class equality — auto-generated equals/hashCode ===")
    data class Point(val x: Int, val y: Int)

    val p1 = Point(1, 2)
    val p2 = Point(1, 2)
    println("Point(1,2) == Point(1,2): ${p1 == p2}")    // true — data class equals
    println("Point(1,2) === Point(1,2): ${p1 === p2}")   // false — different objects

    println("\n=== Regular class — no auto equals ===")
    class RegularPoint(val x: Int, val y: Int)

    val r1 = RegularPoint(1, 2)
    val r2 = RegularPoint(1, 2)
    println("RegularPoint(1,2) == RegularPoint(1,2): ${r1 == r2}")   // false!
    println("(uses identity-based equals by default)")

    println("\n=== Array equality trap ===")
    val arr1 = arrayOf(1, 2, 3)
    val arr2 = arrayOf(1, 2, 3)
    println("arrayOf(1,2,3) == arrayOf(1,2,3): ${arr1 == arr2}")                // false!
    println("contentEquals: ${arr1.contentEquals(arr2)}")                         // true
    println("Arrays use identity equality, not content equality with ==")
}
