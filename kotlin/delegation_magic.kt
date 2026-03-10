// Experiment: Kotlin's delegation — "by" keyword replaces boilerplate and enables patterns
// that would need verbose code in Java.

import kotlin.properties.Delegates
import kotlin.reflect.KProperty

interface Printer {
    fun print(msg: String)
}

class ConsolePrinter : Printer {
    override fun print(msg: String) = println("  [Console] $msg")
}

// Delegate ALL Printer methods to the instance — zero boilerplate
class PrefixPrinter(printer: Printer) : Printer by printer

class Trimmed {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): String = value
    operator fun setValue(thisRef: Any?, property: KProperty<*>, newValue: String) {
        value = newValue.trim()
        println("  ${property.name} set to '$value' (trimmed)")
    }
    private var value: String = ""
}

class MapBacked(private val map: Map<String, Any?>) {
    val name: String by map
    val age: Int by map
}

fun main() {
    println("=== lazy — computed once on first access ===")
    val expensiveValue: String by lazy {
        println("  Computing expensive value...")
        "result-${System.currentTimeMillis()}"
    }
    println("Before first access")
    println("First access: $expensiveValue")
    println("Second access: $expensiveValue")  // Same value, no recomputation

    println("\n=== observable — react to changes ===")
    var observed: String by Delegates.observable("initial") { prop, old, new ->
        println("  ${prop.name}: '$old' → '$new'")
    }
    observed = "changed"
    observed = "changed again"

    println("\n=== vetoable — reject invalid changes ===")
    var positive: Int by Delegates.vetoable(1) { _, _, new ->
        new > 0  // Only accept positive values
    }
    println("positive = $positive")
    positive = 10
    println("After setting 10: $positive")
    positive = -5
    println("After setting -5: $positive")  // Still 10! Change was vetoed

    println("\n=== Custom delegate — map-backed properties ===")
    val person = MapBacked(mapOf("name" to "Alice", "age" to 30))
    println("From map → name=${person.name}, age=${person.age}")

    println("\n=== Custom delegate from scratch ===")
    var input: String by Trimmed()
    input = "  lots of spaces  "
    println("Trimmed result: '$input'")

    println("\n=== Interface delegation — composition over inheritance ===")
    val printer = PrefixPrinter(ConsolePrinter())
    printer.print("Hello via delegation!")
}
