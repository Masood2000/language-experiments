// Experiment: Kotlin's null safety system — the compiler prevents null crashes at compile time
// But there are surprising edge cases and ways nulls can still sneak in.

fun main() {
    println("=== Safe call chaining ===")
    val name: String? = null
    // In Java this would be: if (name != null && name.length() > 0) ...
    // In Kotlin, the ?. operator short-circuits to null
    println("null?.length = ${name?.length}")
    println("null?.length?.plus(1) = ${name?.length?.plus(1)}")

    println("\n=== Elvis operator ?: ===")
    val len = name?.length ?: -1
    println("(null?.length) ?: -1 = $len")

    // Elvis with throw — common pattern for non-null assertions with good errors
    // val mustExist = name ?: throw IllegalArgumentException("name required")

    println("\n=== Smart casts — the compiler tracks null checks ===")
    val maybe: String? = "hello"
    if (maybe != null) {
        // Compiler knows maybe is String (not String?) here
        println("After null check, length = ${maybe.length}")  // No ?. needed!
    }

    println("\n=== Platform types — nulls sneaking in from Java ===")
    // Java methods return "platform types" (String!) — neither String nor String?
    // The compiler can't check these, so NPE is possible
    val javaString: String = System.getProperty("nonexistent") ?: "fallback"
    println("Java interop with fallback: $javaString")

    println("\n=== !! operator — the deliberate crash ===")
    val value: String? = "exists"
    println("value!! = ${value!!}")  // Works, but throws NPE if null
    // This is Kotlin saying: "I know better than the compiler"

    println("\n=== let + safe call — scoping non-null values ===")
    val items: List<String?> = listOf("a", null, "b", null, "c")
    print("Non-null items: ")
    items.forEach { it?.let { item -> print("$item ") } }
    println()

    println("\n=== Nothing type — the bottom type ===")
    // Functions that never return have return type Nothing
    // Nothing is a subtype of every type, including Nothing?
    fun fail(msg: String): Nothing = throw RuntimeException(msg)

    val result: String = try {
        fail("demo")
    } catch (e: RuntimeException) {
        "caught: ${e.message}"
    }
    println("Nothing return caught: $result")
}
