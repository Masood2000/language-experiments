// Experiment: Extension functions — Kotlin lets you add methods to existing classes
// But they're resolved statically, not dynamically. This creates surprises.

open class Animal
class Dog : Animal()

fun Animal.sound() = "..."
fun Dog.sound() = "Woof!"

fun String.wordCount(): Int = this.split("\\s+".toRegex()).size

// Nullable receiver — extensions on null!
fun String?.safeLength(): Int = this?.length ?: 0

// Extension properties
val List<Int>.secondOrNull: Int?
    get() = if (size >= 2) this[1] else null

fun main() {
    println("=== Adding methods to existing classes ===")
    println("\"hello world foo\".wordCount() = ${"hello world foo".wordCount()}")

    println("\n=== Extensions are resolved STATICALLY ===")
    val animal: Animal = Dog()  // Static type is Animal, runtime type is Dog
    println("val animal: Animal = Dog()")
    println("animal.sound() = ${animal.sound()}")  // "..." — NOT "Woof!"
    println("Extension resolved by COMPILE-TIME type, not runtime type!")

    val dog: Dog = Dog()
    println("val dog: Dog = Dog()")
    println("dog.sound() = ${dog.sound()}")  // "Woof!" — compile-time type is Dog

    println("\n=== Member functions always win over extensions ===")
    class Greeter {
        fun greet() = "Hello from member"
    }
    fun Greeter.greet() = "Hello from extension"
    println("Greeter().greet() = ${Greeter().greet()}")  // Member wins!

    println("\n=== Nullable receiver — extensions on null ===")
    val nullStr: String? = null
    println("null.safeLength() = ${nullStr.safeLength()}")  // Works! No NPE
    println("You can call extension functions on null!")

    println("\n=== Extension properties ===")
    println("listOf(10,20,30).secondOrNull = ${listOf(10, 20, 30).secondOrNull}")
    println("listOf(10).secondOrNull = ${listOf(10).secondOrNull}")

    println("\n=== Scope functions use extensions internally ===")
    val result = "Hello"
        .let { it.uppercase() }
        .also { println("  also: $it") }
        .run { "${this}!" }
    println("Chained scope functions: $result")
}
