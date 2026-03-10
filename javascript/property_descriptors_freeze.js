/**
 * property_descriptors_freeze.js
 *
 * Explores hidden property attributes (writable, enumerable, configurable),
 * Object.freeze() shallow behavior, and non-enumerable property quirks.
 */

console.log("=== Property Descriptors & Object.freeze() ===\n");

// Experiment 1: Properties have hidden attributes
console.log("1. Property descriptor attributes:");
const obj = { visible: 1 };
Object.defineProperty(obj, 'hidden', {
    value: 42,
    enumerable: false,
    writable: false,
    configurable: false
});
console.log("   obj.hidden =", obj.hidden, "(directly accessible)");
console.log("   Object.keys(obj) =", JSON.stringify(Object.keys(obj)));
console.log("   JSON.stringify(obj) =", JSON.stringify(obj));
console.log("   'hidden' in obj =", 'hidden' in obj);
console.log("   Hidden from keys/JSON but still accessible and detectable!");

console.log();

// Experiment 2: Object.freeze is shallow
console.log("2. Object.freeze() is shallow:");
const frozen = Object.freeze({
    name: "immutable",
    nested: { value: "mutable!" }
});
frozen.name = "changed";
console.log("   frozen.name =", frozen.name, "(unchanged -- frozen)");
frozen.nested.value = "I was changed!";
console.log("   frozen.nested.value =", frozen.nested.value, "(changed!)");
console.log("   Nested objects are NOT frozen -- only top-level properties are");

console.log();

// Experiment 3: Non-enumerable properties survive spread
console.log("3. Non-enumerable vs spread/Object.assign:");
const src = Object.create(null, {
    enumProp: { value: 1, enumerable: true },
    nonEnumProp: { value: 2, enumerable: false }
});
const spread = { ...src };
console.log("   src.nonEnumProp =", src.nonEnumProp);
console.log("   spread.nonEnumProp =", spread.nonEnumProp);
console.log("   Spread and Object.assign skip non-enumerable properties!");

console.log();

// Experiment 4: Frozen object checks
console.log("4. Object.isFrozen edge cases:");
console.log("   Object.isFrozen(42) =", Object.isFrozen(42), "(primitives are frozen)");
console.log("   Object.isFrozen('str') =", Object.isFrozen('str'));
const sealed = Object.seal({});
console.log("   Object.isFrozen(Object.seal({})) =", Object.isFrozen(sealed));
console.log("   An empty sealed object IS frozen (no writable props to check)!");

console.log();

// Experiment 5: Writable false vs configurable false
console.log("5. writable:false vs configurable:false:");
const demo = {};
Object.defineProperty(demo, 'x', { value: 1, writable: false, configurable: true });
// Can't assign...
demo.x = 99;
console.log("   After demo.x = 99:", demo.x, "(writable:false blocks assignment)");
// But CAN redefine because configurable is true!
Object.defineProperty(demo, 'x', { value: 99 });
console.log("   After defineProperty:", demo.x, "(configurable:true allows redefine!)");

console.log();

// Experiment 6: Array.length is non-configurable but writable
console.log("6. Array.length descriptor:");
const arr = [1, 2, 3, 4, 5];
console.log("   Before:", JSON.stringify(arr));
arr.length = 2;
console.log("   After arr.length = 2:", JSON.stringify(arr));
console.log("   Setting length truncates the array! Elements are permanently lost");

console.log();

// Experiment 7: Getters/setters with defineProperty
console.log("7. Property that lies about its value:");
const liar = {};
let realValue = 0;
Object.defineProperty(liar, 'count', {
    get() { return realValue * 2; },  // Always doubles the real value
    set(v) { realValue = v; },
    enumerable: true
});
liar.count = 5;
console.log("   Set liar.count = 5");
console.log("   liar.count =", liar.count, "(getter returns doubled value)");
console.log("   JSON.stringify =", JSON.stringify(liar), "(JSON calls the getter)");
