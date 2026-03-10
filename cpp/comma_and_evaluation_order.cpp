/**
 * comma_and_evaluation_order.cpp
 *
 * Demonstrates the comma operator, overloaded comma gotchas, C++17
 * evaluation order guarantees, and operator precedence traps.
 */

#include <iostream>
#include <map>
#include <string>

// Custom type that overloads comma operator
struct Collector {
    std::string data;

    Collector() = default;
    Collector(const std::string& s) : data(s) {}

    Collector operator,(const Collector& other) const {
        return Collector(data + ", " + other.data);
    }
};

int main() {
    std::cout << "=== Comma & Evaluation Order ===" << std::endl << std::endl;

    // Experiment 1: Comma operator returns last value
    std::cout << "1. Comma operator returns last value:" << std::endl;
    int a = (1, 2, 3);
    std::cout << "   (1, 2, 3) = " << a << std::endl;

    std::cout << std::endl;

    // Experiment 2: Overloaded comma changes semantics
    std::cout << "2. Overloaded comma operator:" << std::endl;
    Collector result = (Collector("a"), Collector("b"), Collector("c"));
    std::cout << "   (Collector(\"a\"), Collector(\"b\"), Collector(\"c\")) = " << result.data << std::endl;
    std::cout << "   Overloaded comma KEEPS all values instead of discarding!" << std::endl;

    std::cout << std::endl;

    // Experiment 3: map[] silently creates entries
    std::cout << "3. map::operator[] silently creates entries:" << std::endl;
    std::map<std::string, int> m;
    m["exists"] = 42;
    std::cout << "   map size before: " << m.size() << std::endl;
    int val = m["ghost"];  // Accessing non-existent key INSERTS it!
    std::cout << "   Accessed m[\"ghost\"] (doesn't exist)" << std::endl;
    std::cout << "   map size after: " << m.size() << std::endl;
    std::cout << "   m[\"ghost\"] = " << val << " (default-constructed)" << std::endl;
    std::cout << "   Reading from map[] has a write side effect!" << std::endl;

    std::cout << std::endl;

    // Experiment 4: Short-circuit vs bitwise operators
    std::cout << "4. Short-circuit (&&) vs bitwise (&) operators:" << std::endl;
    int x = 0;
    bool sc = (x != 0) && (10 / x > 1);  // Safe: short-circuits
    std::cout << "   (0 != 0) && (10/0 > 1) = " << sc << " (safe, short-circuited)" << std::endl;
    // (x != 0) & (10 / x > 1) would crash -- & doesn't short-circuit!
    std::cout << "   (0 != 0) & (10/0 > 1) would CRASH -- & evaluates both sides" << std::endl;

    std::cout << std::endl;

    // Experiment 5: Operator precedence traps
    std::cout << "5. Bitwise operator precedence trap:" << std::endl;
    int flags = 5;  // binary: 101
    // Common bug: & has LOWER precedence than ==
    if (flags & 1 == 1) {  // Parsed as: flags & (1 == 1) = flags & 1 = 1
        std::cout << "   'flags & 1 == 1' is TRUE" << std::endl;
    }
    // What you probably meant:
    if ((flags & 1) == 1) {
        std::cout << "   '(flags & 1) == 1' is also TRUE (but different expression!)" << std::endl;
    }
    // Show the difference with flags = 4
    int f2 = 4;  // binary: 100
    std::cout << "   flags=4: 'f2 & 1 == 1' = " << (f2 & 1 == 1) << std::endl;
    std::cout << "   flags=4: '(f2 & 1) == 1' = " << ((f2 & 1) == 1) << std::endl;
    std::cout << "   Different results! & has lower precedence than ==" << std::endl;

    std::cout << std::endl;

    // Experiment 6: Ternary operator type coercion
    std::cout << "6. Ternary type coercion surprise:" << std::endl;
    bool cond = true;
    auto r1 = cond ? 1 : 2.5;  // int and double -> double
    std::cout << "   true ? 1 : 2.5 = " << r1 << " (type: double, even though 1 was chosen!)" << std::endl;
    std::cout << "   Both branches must have a common type" << std::endl;

    // Null pointer ternary
    const char* str = cond ? "hello" : nullptr;
    std::cout << "   true ? \"hello\" : nullptr = " << str << std::endl;
    std::cout << "   nullptr is valid as the other branch of const char*" << std::endl;

    std::cout << std::endl;

    // Experiment 7: C++17 guaranteed evaluation order
    std::cout << "7. C++17 evaluation order guarantees:" << std::endl;
    std::string s = "initial";
    // Before C++17: f(s, s = "changed") was undefined behavior
    // C++17: function arguments are still unsequenced (implementation-defined order)
    // But: a.b, a->b, a[b], a<<b, a>>b are now left-to-right
    std::map<int, int> ordered;
    int i = 0;
    ordered[i++] = i++;  // Still problematic -- assignment operands unsequenced
    std::cout << "   C++17 guarantees: a.b, a->b, a[b], <<, >> are left-to-right" << std::endl;
    std::cout << "   But function arguments are still unsequenced!" << std::endl;
    std::cout << "   f(i++, i++) is STILL undefined behavior in C++17" << std::endl;

    return 0;
}
