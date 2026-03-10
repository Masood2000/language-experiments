/**
 * mutating_inout_surprises.swift
 *
 * Demonstrates how inout uses copy-in/copy-out semantics (not pass-by-reference),
 * and how mutating methods interact with property observers.
 */

// Property observer demonstration
struct Counter {
    var value: Int = 0 {
        willSet { print("   willSet: \(value) -> \(newValue)") }
        didSet { print("   didSet: \(oldValue) -> \(value)") }
    }
}

struct Point {
    var x: Double
    var y: Double

    // Computed property
    var magnitude: Double {
        get { return (x * x + y * y).squareRoot() }
        set {
            let scale = newValue / magnitude
            x *= scale
            y *= scale
        }
    }
}

func doubleValue(_ x: inout Int) {
    x *= 2
}

func main() {
    print("=== Mutating & inout Surprises ===\n")

    // Experiment 1: inout uses copy-in/copy-out, not pass-by-reference
    print("1. inout is copy-in/copy-out, NOT pass-by-reference:")
    var a = 10
    print("   Before: a = \(a)")
    doubleValue(&a)
    print("   After doubleValue(&a): a = \(a)")
    print("   Looks like pass-by-reference, but it's copy-in/copy-out")

    print()

    // Experiment 2: inout triggers property observers
    print("2. inout triggers willSet/didSet even if value doesn't change:")
    var counter = Counter(value: 5)
    print("   Calling doubleValue(&counter.value):")
    doubleValue(&counter.value)
    print("   counter.value = \(counter.value)")
    print("   Observers fire because of copy-in/copy-out!")

    print()

    // Experiment 3: inout with computed properties
    print("3. inout works with computed properties:")
    var point = Point(x: 3.0, y: 4.0)
    print("   Point(\(point.x), \(point.y)), magnitude = \(point.magnitude)")
    func doubleMagnitude(_ val: inout Double) {
        val *= 2
    }
    doubleMagnitude(&point.magnitude)  // Works with computed property!
    print("   After doubling magnitude: Point(\(point.x), \(point.y))")
    print("   Computed property getter called, then setter with modified value")

    print()

    // Experiment 4: Can't pass same variable as two inout parameters
    print("4. Can't alias inout parameters:")
    print("   func swap(&a, &a) won't compile -- exclusive access rule")
    print("   Swift enforces memory safety at compile time")
    var arr = [1, 2, 3]
    // swap(&arr[0], &arr[1])  // ERROR: overlapping access
    print("   swap(&arr[0], &arr[1]) is illegal -- overlapping access!")
    arr.swapAt(0, 1)  // Use this instead
    print("   Use arr.swapAt(0, 1) instead: \(arr)")

    print()

    // Experiment 5: mutating on value types
    print("5. mutating modifies self in value types:")
    struct Vector {
        var x: Double
        var y: Double

        mutating func normalize() {
            let len = (x * x + y * y).squareRoot()
            x /= len
            y /= len
        }

        // mutating can completely replace self!
        mutating func reset() {
            self = Vector(x: 0, y: 0)
        }
    }

    var v = Vector(x: 3, y: 4)
    print("   Before normalize: (\(v.x), \(v.y))")
    v.normalize()
    print("   After normalize: (\(v.x), \(v.y))")
    v.reset()
    print("   After reset (self = new): (\(v.x), \(v.y))")
    print("   mutating can replace self entirely!")

    print()

    // Experiment 6: let prevents calling mutating methods
    print("6. let prevents mutating methods:")
    let immutable = Vector(x: 1, y: 2)
    // immutable.normalize()  // ERROR: cannot use mutating on let constant
    print("   let v = Vector(...)")
    print("   v.normalize() won't compile -- let makes value types truly immutable")
    print("   immutable = (\(immutable.x), \(immutable.y))")

    print()

    // Experiment 7: inout captures in closures
    print("7. inout and closures don't mix:")
    print("   func foo(_ x: inout Int) {")
    print("       let closure = { print(x) }  // ERROR!")
    print("   }")
    print("   Closures can't capture inout parameters")
    print("   The parameter might not exist when the closure runs!")
    print("   (copy-out already happened)")
}

main()
