package main

import "fmt"

/*
 * variable_shadowing.go
 *
 * Demonstrates how Go's := operator and block scoping can silently
 * shadow variables, leading to bugs that the compiler won't catch.
 */

func main() {
	fmt.Println("=== Variable Shadowing Gotchas ===")
	fmt.Println()

	// Experiment 1: := in inner block shadows outer variable
	fmt.Println("1. := in inner block creates a NEW variable:")
	x := "outer"
	fmt.Printf("   Before if: x = %s\n", x)
	if true {
		x := "inner" // This creates a NEW x, doesn't modify outer
		fmt.Printf("   Inside if: x = %s\n", x)
	}
	fmt.Printf("   After if: x = %s (unchanged!)\n", x)
	fmt.Println("   The inner x was a different variable entirely")

	fmt.Println()

	// Experiment 2: Multi-return := shadows even when one var exists
	fmt.Println("2. Multi-return := in inner scope shadows:")
	err := fmt.Errorf("original error")
	fmt.Printf("   Before: err = %v\n", err)
	if true {
		// := is required here because val is new, but err also becomes new!
		val, err := doSomething()
		fmt.Printf("   Inside: val = %d, err = %v\n", val, err)
	}
	fmt.Printf("   After: err = %v (still original!)\n", err)
	fmt.Println("   The err inside the if was a DIFFERENT variable")

	fmt.Println()

	// Experiment 3: Named return values can be shadowed
	fmt.Println("3. Named return value shadowing:")
	result, e := namedReturnShadow()
	fmt.Printf("   namedReturnShadow() = %d, %v\n", result, e)
	fmt.Println("   Expected 42, got 0 -- named return was shadowed!")

	fmt.Println()

	// Experiment 4: Loop variable shadowing
	fmt.Println("4. Shadowing loop variable:")
	for i := 0; i < 3; i++ {
		i := i * 10 // Shadows loop variable with local copy
		fmt.Printf("   i = %d", i)
	}
	fmt.Println()
	fmt.Println("   Loop still runs 3 times -- outer i is untouched")

	fmt.Println()

	// Experiment 5: You can shadow builtins
	fmt.Println("5. Shadowing builtins (true, len, nil):")
	true := false
	fmt.Printf("   true = %v (we shadowed the builtin!)\n", true)

	len := func(s string) int { return 999 }
	fmt.Printf("   len(\"hello\") = %d (custom len function!)\n", len("hello"))
	fmt.Println("   Go builtins are NOT reserved words -- they can be shadowed")

	fmt.Println()

	// Experiment 6: Shadowing in switch cases
	fmt.Println("6. Shadowing in switch:")
	val := "original"
	switch val := 42; {
	case val > 0:
		fmt.Printf("   Inside switch: val = %d (int)\n", val)
	}
	fmt.Printf("   Outside switch: val = %s (string, unchanged)\n", val)
}

func doSomething() (int, error) {
	return 99, nil
}

func namedReturnShadow() (result int, err error) {
	// result is the named return, initialized to 0
	if true {
		result, err := 42, error(nil) // SHADOWS the named returns!
		_ = result
		_ = err
	}
	return // Returns named values (still 0, nil) -- not 42!
}
