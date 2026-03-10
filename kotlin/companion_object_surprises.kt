/**
 * companion_object_surprises.kt
 *
 * Demonstrates that companion objects are real singleton objects,
 * not just static member containers. They can implement interfaces,
 * have their own state, and have surprising initialization behavior.
 */

// Interface that a companion object can implement
interface Factory<T> {
    fun create(): T
}

interface Describable {
    fun describe(): String
}

class Widget private constructor(val name: String) {
    companion object : Factory<Widget>, Describable {
        private var count = 0

        override fun create(): Widget {
            count++
            return Widget("Widget #$count")
        }

        override fun describe(): String = "I'm a Widget factory (created $count so far)"
    }
}

// Initialization order demonstration
class InitOrder {
    init {
        println("   Instance init block")
    }

    companion object {
        init {
            println("   Companion init block (runs ONCE on first access)")
        }

        val stamp = run {
            println("   Companion property initialized")
            "companion-ready"
        }
    }
}

// Companion with custom name
class Server {
    companion object Config {
        val host = "localhost"
        val port = 8080
    }
}

fun main() {
    println("=== Companion Object Surprises ===\n")

    // Experiment 1: Companion objects implement interfaces
    println("1. Companion object implements interfaces:")
    val w1 = Widget.create()
    val w2 = Widget.create()
    println("   Widget.create() = ${w1.name}")
    println("   Widget.create() = ${w2.name}")
    println("   Widget.describe() = ${Widget.describe()}")
    println("   Companion IS a Factory<Widget> and Describable!")

    println()

    // Experiment 2: Pass companion object as interface reference
    println("2. Companion object passed as interface reference:")
    fun <T> buildTwo(factory: Factory<T>): List<T> {
        return listOf(factory.create(), factory.create())
    }
    val widgets = buildTwo(Widget)  // Widget itself IS the Factory!
    println("   buildTwo(Widget) = ${widgets.map { it.name }}")
    println("   Widget is passed directly as a Factory<Widget> argument!")

    println()

    // Experiment 3: Initialization order
    println("3. Companion object initialization order:")
    println("   First access to InitOrder.stamp:")
    println("   stamp = ${InitOrder.stamp}")
    println()
    println("   Creating instance of InitOrder:")
    val obj = InitOrder()
    println("   Companion init runs BEFORE any instance init, and only once")

    println()

    // Experiment 4: Named companion objects
    println("4. Named companion objects:")
    println("   Server.Config.host = ${Server.Config.host}")
    println("   Server.Config.port = ${Server.Config.port}")
    println("   Also accessible as Server.host = ${Server.host}")
    println("   Named companions give clearer access patterns")

    println()

    // Experiment 5: Companion objects are real objects with identity
    println("5. Companion objects are real objects:")
    val companion = Widget.Companion
    println("   Widget.Companion is ${companion::class}")
    println("   Widget.Companion === Widget.Companion: ${companion === Widget.Companion}")
    println("   It's a singleton object, not a class/namespace")

    println()

    // Experiment 6: Companion vs top-level vs object
    println("6. Companion vs object declaration:")
    println("   companion object -- tied to a class, accessed via ClassName")
    println("   object Singleton  -- standalone singleton")
    println("   Both are real objects, but companion has special access")
    println("   Companion can access private members of its enclosing class!")

    println()

    // Experiment 7: Companion object mutable state (shared across instances)
    println("7. Companion state is shared (like static fields):")
    println("   Total widgets created: ${Widget.describe()}")
    Widget.create()
    Widget.create()
    println("   After 2 more: ${Widget.describe()}")
    println("   State persists in the companion singleton")
}
