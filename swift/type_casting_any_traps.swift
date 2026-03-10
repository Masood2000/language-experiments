/**
 * type_casting_any_traps.swift
 *
 * Demonstrates how Any silently wraps optionals, numeric casting through
 * Any fails, and other type system surprises.
 */

protocol Displayable {
    func display() -> String
}
extension Int: Displayable {
    func display() -> String { return "Int: \(self)" }
}
extension String: Displayable {
    func display() -> String { return "String: \(self)" }
}

func main() {
    print("=== Type Casting & Any Traps ===\n")

    // Experiment 1: Any silently wraps optionals
    print("1. Any silently wraps optionals:")
    let optionalString: String? = "hello"
    let any: Any = optionalString as Any
    print("   optionalString: String? = \"hello\"")
    print("   let any: Any = optionalString")
    print("   any is String: \(any is String)")
    print("   any is String?: \(any is String?)")
    print("   type(of: any) = \(type(of: any))")
    print("   Optional<String> wrapped inside Any!")

    print()

    // Experiment 2: nil wrapped in Any is confusing
    print("2. nil in Any:")
    let nilValue: String? = nil
    let anyNil: Any = nilValue as Any
    print("   nilValue: String? = nil")
    print("   anyNil: Any = nilValue")
    print("   anyNil is String: \(anyNil is String)")
    print("   type(of: anyNil) = \(type(of: anyNil))")
    if case Optional<String>.none = anyNil {
        print("   Pattern matching finds it's Optional<String>.none")
    }

    print()

    // Experiment 3: Numeric casts through Any fail
    print("3. Numeric casting through Any fails:")
    let intVal: Int = 42
    let anyInt: Any = intVal
    print("   let anyInt: Any = 42 (Int)")
    print("   anyInt as? Double = \(String(describing: anyInt as? Double))")
    print("   anyInt as? Float = \(String(describing: anyInt as? Float))")
    print("   anyInt as? Int = \(String(describing: anyInt as? Int))")
    print("   Int-to-Double cast that normally works FAILS through Any!")
    print("   Any preserves the exact original type")

    print()

    // Experiment 4: T.self vs type(of:) with Any
    print("4. T.self vs type(of:) diverge with generics:")
    let value: Any = 42
    print("   let value: Any = 42")
    print("   type(of: value) = \(type(of: value))")

    func checkType<T>(_ x: T) {
        print("   Generic T type: \(T.self)")
        print("   Actual type: \(type(of: x))")
    }
    checkType(value)
    print("   T.self sees the declared type, type(of:) sees the actual type!")

    print()

    // Experiment 5: Protocol conformance check through Any
    print("5. Protocol conformance through Any:")
    let items: [Any] = [42, "hello", 3.14, true]
    for item in items {
        if let p = item as? Displayable {
            print("   \(type(of: item)) conforms: \(p.display())")
        } else {
            print("   \(type(of: item)) does NOT conform to Displayable")
        }
    }
    print("   Protocol conformance check works through Any!")

    print()

    // Experiment 6: AnyObject vs Any
    print("6. AnyObject vs Any:")
    print("   Any -- holds ANY type (value types, reference types, functions)")
    print("   AnyObject -- holds only CLASS instances (reference types)")

    class MyClass {}
    struct MyStruct {}

    let _classInstance: AnyObject = MyClass()
    let anyStruct: Any = MyStruct()
    print("   MyClass() as AnyObject: OK")
    print("   MyStruct() as AnyObject: compile error!")
    print("   MyStruct() as Any: \(type(of: anyStruct))")

    print()

    // Experiment 7: as! force cast dangers
    print("7. Force cast (as!) vs conditional (as?):")
    let mystery: Any = "hello"
    if let str = mystery as? String {
        print("   as? safely unwraps: \(str)")
    }
    if let num = mystery as? Int {
        print("   This won't print: \(num)")
    } else {
        print("   as? returns nil for wrong type (safe)")
    }
    print("   as! would crash here -- always prefer as?")
}

main()
