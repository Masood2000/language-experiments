// Experiment: Collections vs Sequences — eager vs lazy evaluation
// Kotlin collections process all items at each step. Sequences process one item through all steps.

fun main() {
    println("=== Eager collection — creates intermediate lists ===")
    val eagerResult = (1..10)
        .map {
            print("map($it) ")
            it * it
        }
        .filter {
            print("filter($it) ")
            it > 20
        }
        .first()
    println("\nEager result: $eagerResult")
    println("ALL items were mapped, then ALL were filtered, then first() picked one")

    println("\n=== Lazy sequence — processes item by item ===")
    val lazyResult = (1..10)
        .asSequence()
        .map {
            print("map($it) ")
            it * it
        }
        .filter {
            print("filter($it) ")
            it > 20
        }
        .first()
    println("\nLazy result: $lazyResult")
    println("Stopped as soon as first match was found!")

    println("\n=== Infinite sequences ===")
    val fibs = generateSequence(Pair(0L, 1L)) { (a, b) -> Pair(b, a + b) }
        .map { it.first }

    println("First 10 Fibonacci: ${fibs.take(10).toList()}")
    println("First Fibonacci > 1000: ${fibs.first { it > 1000 }}")
    // This would be impossible with eager collections!

    println("\n=== Order of operations matters ===")
    // filter-then-map vs map-then-filter
    val data = (1..1_000_000).asSequence()

    var filterCount = 0
    var mapCount = 0

    // Filter first (fewer maps needed)
    data.filter { filterCount++; it % 2 == 0 }
        .map { mapCount++; it * 2 }
        .take(5)
        .toList()
    println("Filter-first: $filterCount filters, $mapCount maps")

    filterCount = 0
    mapCount = 0

    // Map first (maps everything before filtering)
    data.map { mapCount++; it * 2 }
        .filter { filterCount++; it % 4 == 0 }
        .take(5)
        .toList()
    println("Map-first: $filterCount filters, $mapCount maps")

    println("\n=== Sequence vs Collection — when to use which ===")
    // Sequences win: large data, chained operations, early termination (first/take)
    // Collections win: small data, single operation, need indexing/size
    println("Rule: Use sequences for chains of 2+ operations on large/unknown-size data")
}
