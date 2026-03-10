/**
 * compound_literal_lifetime.c
 *
 * Demonstrates compound literals in C: they're mutable lvalues with
 * block-scoped lifetime, unlike string literals. Each evaluation
 * creates a unique object.
 */

#include <stdio.h>
#include <string.h>

struct Point {
    int x, y;
};

void print_point(const struct Point *p) {
    printf("(%d, %d)", p->x, p->y);
}

// Function that takes a struct pointer -- no need to declare a variable!
void move_point(struct Point *p, int dx, int dy) {
    p->x += dx;
    p->y += dy;
}

int main() {
    printf("=== Compound Literal Lifetime ===\n\n");

    // Experiment 1: Compound literals are lvalues (have addresses)
    printf("1. Compound literals are lvalues:\n");
    struct Point *p = &(struct Point){10, 20};
    printf("   &(struct Point){10, 20} = ");
    print_point(p);
    printf("\n   You can take the address of a compound literal!\n");

    printf("\n");

    // Experiment 2: Compound literals are mutable (unlike string literals)
    printf("2. Compound literals are MUTABLE:\n");
    struct Point *q = &(struct Point){1, 2};
    printf("   Before: ");
    print_point(q);
    q->x = 100;
    q->y = 200;
    printf("\n   After mutation: ");
    print_point(q);
    printf("\n   Unlike string literals, compound literals can be modified!\n");

    printf("\n");

    // Experiment 3: Pass structs to functions without declaring variables
    printf("3. Pass struct directly to function (no variable needed):\n");
    struct Point tmp = (struct Point){5, 5};
    move_point(&tmp, 3, 4);
    printf("   move_point(&(Point){5,5}, 3, 4) = ");
    print_point(&tmp);
    printf("\n   No variable declaration needed for function arguments!\n");

    printf("\n");

    // Experiment 4: Each compound literal is a unique object
    printf("4. Each compound literal is a unique object:\n");
    int *a = (int[]){42};
    int *b = (int[]){42};
    printf("   (int[]){42} at %p\n", (void*)a);
    printf("   (int[]){42} at %p\n", (void*)b);
    printf("   Same expression, different objects: %s\n", a == b ? "SAME" : "DIFFERENT");
    printf("   Unlike string literals which MAY share storage!\n");

    printf("\n");

    // Experiment 5: Compound literal arrays
    printf("5. Compound literal arrays:\n");
    int *arr = (int[]){10, 20, 30, 40, 50};
    printf("   (int[]){10,20,30,40,50}[2] = %d\n", arr[2]);
    arr[2] = 999;  // Mutable!
    printf("   After arr[2] = 999: %d\n", arr[2]);
    printf("   Arrays from compound literals are mutable too!\n");

    printf("\n");

    // Experiment 6: Compound literals with designated initializers
    printf("6. Compound literals with designated initializers:\n");
    struct Point *r = &(struct Point){.y = 42, .x = 7};
    printf("   (struct Point){.y = 42, .x = 7} = ");
    print_point(r);
    printf("\n   Designated initializers work in compound literals!\n");

    printf("\n");

    // Experiment 7: String compound literal vs string literal
    printf("7. char[] compound literal vs string literal:\n");
    char *str_literal = "hello";   // Points to read-only memory
    char *comp_literal = (char[]){"hello"};  // Points to mutable stack memory
    printf("   String literal at:   %p\n", (void*)str_literal);
    printf("   Compound literal at: %p\n", (void*)comp_literal);
    comp_literal[0] = 'H';  // This is safe!
    printf("   After comp_literal[0] = 'H': %s\n", comp_literal);
    printf("   Compound literal is mutable; string literal would crash!\n");

    return 0;
}
