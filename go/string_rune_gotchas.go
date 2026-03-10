package main

import (
	"fmt"
	"reflect"
	"unicode/utf8"
)

/*
 * string_rune_gotchas.go
 *
 * Demonstrates that Go strings are byte slices, not character arrays.
 * len() counts bytes, indexing returns bytes, and range iterates runes.
 * This creates subtle bugs when working with non-ASCII text.
 */

func main() {
	fmt.Println("=== String & Rune Gotchas ===")
	fmt.Println()

	// Experiment 1: len() counts bytes, not characters
	fmt.Println("1. len() counts bytes, not characters:")
	s := "Hello, 世界"
	fmt.Printf("   \"%s\"\n", s)
	fmt.Printf("   len() = %d (bytes)\n", len(s))
	fmt.Printf("   utf8.RuneCountInString() = %d (characters)\n", utf8.RuneCountInString(s))
	fmt.Println("   '世' takes 3 bytes in UTF-8!")

	fmt.Println()

	// Experiment 2: Indexing returns bytes, not characters
	fmt.Println("2. Indexing returns bytes, not runes:")
	fmt.Printf("   s[0] = %d (type: %s) -- 'H' as byte\n", s[0], reflect.TypeOf(s[0]))
	fmt.Printf("   s[7] = %d (type: %s) -- first byte of '世', NOT '世'\n", s[7], reflect.TypeOf(s[7]))
	// To get rune at position, must use []rune conversion
	runes := []rune(s)
	fmt.Printf("   []rune(s)[7] = %c -- correct character\n", runes[7])

	fmt.Println()

	// Experiment 3: range iterates runes, index loop iterates bytes
	fmt.Println("3. range vs index loop:")
	emoji := "Go🚀"
	fmt.Printf("   \"%s\" (len=%d)\n", emoji, len(emoji))
	fmt.Print("   Index loop bytes: ")
	for i := 0; i < len(emoji); i++ {
		fmt.Printf("%d ", emoji[i])
	}
	fmt.Println()
	fmt.Print("   Range loop runes: ")
	for _, r := range emoji {
		fmt.Printf("%c(U+%04X) ", r, r)
	}
	fmt.Println()
	fmt.Println("   range auto-decodes UTF-8; index loop does not!")

	fmt.Println()

	// Experiment 4: String comparison is byte-wise
	fmt.Println("4. String comparison is byte-level:")
	// Two visually identical strings with different Unicode representations
	s1 := "café"                   // 'é' as single code point U+00E9
	s2 := "cafe\u0301"             // 'e' + combining acute accent U+0301
	fmt.Printf("   s1 = %s (len=%d)\n", s1, len(s1))
	fmt.Printf("   s2 = %s (len=%d)\n", s2, len(s2))
	fmt.Printf("   s1 == s2: %v\n", s1 == s2)
	fmt.Println("   Visually identical but different bytes -- NOT equal!")

	fmt.Println()

	// Experiment 5: String to byte slice conversion copies
	fmt.Println("5. String-to-[]byte conversion copies data:")
	original := "hello"
	bytes := []byte(original)
	bytes[0] = 'H'
	fmt.Printf("   original = %s (unchanged)\n", original)
	fmt.Printf("   bytes = %s (modified copy)\n", string(bytes))
	fmt.Println("   Strings are immutable; []byte conversion creates a copy")

	fmt.Println()

	// Experiment 6: rune is just int32
	fmt.Println("6. rune is an alias for int32:")
	var r rune = 'A'
	var i int32 = 'A'
	fmt.Printf("   rune 'A' = %d, int32 'A' = %d\n", r, i)
	fmt.Printf("   rune == int32: %v\n", r == i)
	fmt.Printf("   Type of rune: %s\n", reflect.TypeOf(r))
	fmt.Println("   rune and int32 are completely interchangeable")
}
