/**
 * comma_operator_tricks.c
 *
 * Demonstrates the comma operator's surprising behaviors: lowest precedence,
 * left-to-right evaluation discarding all but the last value, and
 * unexpected interactions with assignment and macros.
 */

#include <stdio.h>

int main() {
    printf("=== Comma Operator Tricks ===\n\n");

    // Experiment 1: Comma evaluates left-to-right, returns last value
    printf("1. Comma returns the LAST value:\n");
    int a = (1, 2, 3, 4, 5);
    printf("   (1, 2, 3, 4, 5) = %d\n", a);
    printf("   All expressions are evaluated but only 5 is kept\n");

    printf("\n");

    // Experiment 2: Comma vs assignment precedence
    printf("2. Comma has LOWER precedence than assignment:\n");
    int b;
    b = 10, 20;  // This is (b = 10), 20 -- NOT b = (10, 20)
    printf("   b = 10, 20  =>  b = %d (not 20!)\n", b);
    int c = (10, 20);  // Need parens to get 20
    printf("   b = (10, 20) => b = %d\n", c);

    printf("\n");

    // Experiment 3: Side effects in comma expressions
    printf("3. Side effects in comma expressions:\n");
    int x = 0, y = 0;
    int result = (x++, y = x * 10, y + 1);
    printf("   (x++, y = x * 10, y + 1) => %d\n", result);
    printf("   x = %d, y = %d\n", x, y);
    printf("   All side effects happen left-to-right\n");

    printf("\n");

    // Experiment 4: Comma in for loops (legitimate use)
    printf("4. Comma in for loops (common idiom):\n");
    printf("   ");
    for (int i = 0, j = 10; i < j; i++, j--) {
        printf("(%d,%d) ", i, j);
    }
    printf("\n   Multiple variables updated per iteration\n");

    printf("\n");

    // Experiment 5: Comma in array subscript
    printf("5. Comma inside array subscript:\n");
    int arr[] = {10, 20, 30, 40, 50};
    printf("   arr[1, 3] = %d\n", arr[(1, 3)]);  // Evaluates to arr[3]
    printf("   The 1 is evaluated and discarded, 3 is the actual index!\n");

    printf("\n");

    // Experiment 6: Comma introduces a sequence point
    printf("6. Comma introduces a sequence point:\n");
    int val = 0;
    int seq = (val = 1, val + 10);  // Well-defined: val=1 completes before val+10
    printf("   (val = 1, val + 10) = %d\n", seq);
    printf("   Unlike i++ + i++ (undefined), comma makes order guaranteed\n");

    printf("\n");

    // Experiment 7: Comma in ternary operator gotcha
    printf("7. Comma in ternary -- grouping surprise:\n");
    int d = 1 ? 2, 3 : 4;  // Parsed as (1 ? 2, 3 : 4) by some compilers
    // Actually: 1 ? (2, 3) : 4
    printf("   1 ? 2, 3 : 4 = %d\n", d);
    printf("   The comma is part of the 'true' branch!\n");

    return 0;
}
