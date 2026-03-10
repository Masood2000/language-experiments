/**
 * automatic_semicolon_insertion.js
 *
 * Demonstrates how JavaScript's Automatic Semicolon Insertion (ASI)
 * silently changes the meaning of code in surprising ways.
 */

console.log("=== Automatic Semicolon Insertion (ASI) ===\n");

// Experiment 1: return + newline = return undefined
console.log("1. 'return' followed by newline:");
function getObject() {
    return
    {
        name: "hello"
    };
}
console.log("   getObject() =", getObject());
console.log("   Expected: { name: 'hello' }");
console.log("   Actual: undefined -- ASI inserted semicolon after 'return'");

console.log();

// Experiment 2: Lines starting with ( or [ merge with previous line
console.log("2. Lines starting with ( merge with previous:");
let a = 42
let result;
try {
    // The next line looks independent but actually tries to call 42(...)
    // a = 42
    // (function() { console.log("hi") })()
    // becomes: a = 42(function() { ... })()
    result = "If no semicolon, 42(...) would be attempted";
    console.log("   " + result);
} catch (e) {
    console.log("   Error: " + e.message);
}

console.log();

// Experiment 3: Array literal on new line
console.log("3. Array on new line can become property access:");
let x = 1
let y = 2
// If these were on consecutive lines without semicolons:
// x = 1[y, x] would be parsed as x = 1[2,1] = 1[1] = undefined
console.log("   1[1] =", 1[1], "(number has no index property)");

console.log();

// Experiment 4: ASI does NOT insert before ++/--
console.log("4. ASI does NOT insert before ++ or --:");
let counter = 0;
let val = counter
++counter  // This increments counter, not val
console.log("   counter after 'val = counter \\n ++counter':");
console.log("   val =", val, ", counter =", counter);
console.log("   Parsed as: val = counter; ++counter (ASI before ++ on new line)");
// Actually ASI DOES insert here because ++ is a restricted production
// Let's show the real gotcha:

console.log();

// Experiment 5: throw must have expression on same line
console.log("5. 'throw' requires expression on same line:");
try {
    // throw
    // new Error("test")
    // Would be: throw; new Error("test")  -- SyntaxError!
    // We can't demo this directly as it won't parse, but:
    console.log("   'throw\\n new Error()' would be a SyntaxError");
    console.log("   ASI inserts semicolon after 'throw', making it bare 'throw;'");
} catch (e) {
    console.log("   " + e.message);
}

console.log();

// Experiment 6: Template literal on new line
console.log("6. Tagged template gotcha:");
function tag(strings) { return "tagged!"; }
let tagResult = tag
`hello`
console.log("   tag\\n`hello` =", tagResult);
console.log("   Parsed as tag`hello` (tagged template), NOT two statements!");
console.log("   ASI does NOT insert before template literals");

console.log();

// Experiment 7: The void operator saves the day
console.log("7. Safe IIFE patterns:");
console.log("   Unsafe: (function(){})() -- can merge with previous line");
console.log("   Safe:   void function(){}() -- void prevents merging");
console.log("   Safe:   !function(){}() -- ! also prevents merging");
void function() { console.log("   void IIFE executed safely"); }();
