/**
 * lambda_capture_quirks.cpp
 *
 * Demonstrates surprising behaviors with C++ lambda captures: value vs
 * reference semantics, mutable lambdas, init captures, and the loop
 * variable reference capture gotcha.
 */

#include <iostream>
#include <vector>
#include <functional>
#include <typeinfo>

int main() {
    std::cout << "=== Lambda Capture Quirks ===" << std::endl << std::endl;

    // Experiment 1: Value capture takes a snapshot
    std::cout << "1. Value capture is a snapshot:" << std::endl;
    int x = 10;
    auto by_value = [x]() { return x; };
    x = 999;
    std::cout << "   x = " << x << ", but lambda returns " << by_value() << std::endl;
    std::cout << "   Value was captured at creation time, not call time" << std::endl;

    std::cout << std::endl;

    // Experiment 2: Reference capture sees changes
    std::cout << "2. Reference capture sees later changes:" << std::endl;
    int y = 10;
    auto by_ref = [&y]() { return y; };
    y = 999;
    std::cout << "   y = " << y << ", lambda returns " << by_ref() << std::endl;
    std::cout << "   Reference follows the variable" << std::endl;

    std::cout << std::endl;

    // Experiment 3: Value captures are const by default
    std::cout << "3. Value captures are const by default:" << std::endl;
    int z = 10;
    // auto bad = [z]() { z++; };  // Won't compile!
    auto good = [z]() mutable { return ++z; };  // Need 'mutable'
    std::cout << "   [z]() { z++; } won't compile -- captures are const!" << std::endl;
    std::cout << "   [z]() mutable { return ++z; } = " << good() << std::endl;
    std::cout << "   But original z = " << z << " (unchanged)" << std::endl;

    std::cout << std::endl;

    // Experiment 4: Mutable lambda copies have independent state
    std::cout << "4. Each copy of a mutable lambda has independent state:" << std::endl;
    int counter = 0;
    auto make_counter = [counter]() mutable { return counter++; };
    std::cout << "   call 1: " << make_counter() << std::endl;
    std::cout << "   call 2: " << make_counter() << std::endl;

    auto copy = make_counter;  // Copy the lambda
    std::cout << "   original call 3: " << make_counter() << std::endl;
    std::cout << "   copy call (independent): " << copy() << std::endl;
    std::cout << "   Copy has its own state snapshot!" << std::endl;

    std::cout << std::endl;

    // Experiment 5: Init capture (C++14) -- move into lambda
    std::cout << "5. Init capture (generalized capture):" << std::endl;
    auto ptr = std::make_unique<int>(42);
    // Can't copy unique_ptr, but can move it into lambda
    auto owns_ptr = [p = std::move(ptr)]() { return *p; };
    std::cout << "   Lambda owns unique_ptr, returns: " << owns_ptr() << std::endl;
    std::cout << "   ptr is now " << (ptr ? "valid" : "null") << " (moved out)" << std::endl;

    std::cout << std::endl;

    // Experiment 6: Loop variable reference capture gotcha
    std::cout << "6. Loop variable reference capture gotcha:" << std::endl;
    std::vector<std::function<int()>> funcs;
    for (int i = 0; i < 5; i++) {
        funcs.push_back([&i]() { return i; });  // Captures reference to i
    }
    std::cout << "   All lambdas captured &i. After loop, i = 5:" << std::endl;
    std::cout << "   ";
    for (auto& f : funcs) {
        std::cout << f() << " ";  // All return 5!
    }
    std::cout << std::endl;

    // Fix: capture by value
    std::vector<std::function<int()>> fixed;
    for (int i = 0; i < 5; i++) {
        fixed.push_back([i]() { return i; });  // Capture by value
    }
    std::cout << "   Fixed (capture by value): ";
    for (auto& f : fixed) {
        std::cout << f() << " ";
    }
    std::cout << std::endl;

    std::cout << std::endl;

    // Experiment 7: Each lambda has a unique type
    std::cout << "7. Each lambda expression has a UNIQUE type:" << std::endl;
    auto l1 = [](int x) { return x; };
    auto l2 = [](int x) { return x; };
    std::cout << "   l1 type: " << typeid(l1).name() << std::endl;
    std::cout << "   l2 type: " << typeid(l2).name() << std::endl;
    std::cout << "   Same body, but " << (typeid(l1) == typeid(l2) ? "SAME" : "DIFFERENT") << " types!" << std::endl;
    std::cout << "   Can't store in same variable without std::function" << std::endl;

    return 0;
}
