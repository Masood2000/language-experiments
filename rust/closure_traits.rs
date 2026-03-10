/// closure_traits.rs
///
/// Demonstrates how Rust closures automatically implement Fn/FnMut/FnOnce
/// traits based on how they capture variables, and how the move keyword
/// interacts with Copy types.

fn main() {
    println!("=== Closure Traits in Rust ===\n");

    // Experiment 1: Fn -- borrows immutably
    println!("1. Fn trait -- closure that only reads captures:");
    let name = String::from("Rust");
    let greet = || println!("   Hello, {}!", name);  // Borrows &name
    greet();
    greet();  // Can call multiple times
    println!("   name is still accessible: {}", name);  // Still usable
    println!("   Closure implements Fn (immutable borrow)");

    println!();

    // Experiment 2: FnMut -- borrows mutably
    println!("2. FnMut trait -- closure that mutates captures:");
    let mut count = 0;
    let mut increment = || {
        count += 1;  // Borrows &mut count
        count
    };
    println!("   Call 1: {}", increment());
    println!("   Call 2: {}", increment());
    println!("   Call 3: {}", increment());
    // println!("{}", count);  // Can't use count while increment exists!
    drop(increment);
    println!("   After dropping closure, count = {}", count);
    println!("   Closure implements FnMut (mutable borrow)");

    println!();

    // Experiment 3: FnOnce -- consumes captures
    println!("3. FnOnce trait -- closure that consumes captures:");
    let data = vec![1, 2, 3];
    let consume = || {
        let moved = data;  // Moves data into the closure body
        println!("   Consumed: {:?}", moved);
        moved  // data is now gone
    };
    let _result = consume();
    // consume();  // ERROR: can't call again, data was consumed
    // println!("{:?}", data);  // ERROR: data was moved
    println!("   Can only call once -- data was moved into closure");

    println!();

    // Experiment 4: move keyword with Copy types
    println!("4. 'move' with Copy types -- surprise!:");
    let x: i32 = 42;
    let closure = move || x;  // 'move' copies x (i32 is Copy)
    println!("   Closure returns: {}", closure());
    println!("   x is still accessible: {}", x);  // x was COPIED, not moved!
    println!("   'move' copies Copy types -- original still usable!");

    println!();

    // Experiment 5: move keyword with non-Copy types
    println!("5. 'move' with non-Copy types:");
    let s = String::from("hello");
    let closure = move || s.len();  // s is MOVED into closure
    println!("   closure() = {}", closure());
    // println!("{}", s);  // ERROR: s was moved
    println!("   String was moved -- can't use original anymore");

    println!();

    // Experiment 6: Closures as function parameters
    println!("6. Trait bounds determine which closures are accepted:");

    fn call_fn(f: impl Fn()) { f(); f(); }
    fn call_fn_mut(mut f: impl FnMut()) { f(); f(); }
    fn call_fn_once(f: impl FnOnce()) { f(); }

    let msg = "hello".to_string();

    // Fn can be passed to all three
    let fn_closure = || println!("   Fn: {}", msg);
    call_fn(fn_closure);

    // FnMut can be passed to FnMut and FnOnce but NOT Fn
    let mut counter2 = 0;
    let fn_mut_closure = || { counter2 += 1; println!("   FnMut: count={}", counter2); };
    call_fn_mut(fn_mut_closure);

    // FnOnce can only be passed to FnOnce
    let data2 = vec![1, 2, 3];
    let fn_once_closure = || { let _x = data2; println!("   FnOnce: consumed data"); };
    call_fn_once(fn_once_closure);

    println!("   Fn <: FnMut <: FnOnce (subtype relationship)");

    println!();

    // Experiment 7: Non-capturing closures coerce to function pointers
    println!("7. Non-capturing closures coerce to fn pointers:");
    let closure = |x: i32| x * 2;
    let fn_ptr: fn(i32) -> i32 = closure;  // Works! No captures
    println!("   fn_ptr(21) = {}", fn_ptr(21));

    let factor = 3;
    let capturing = |x: i32| x * factor;
    // let fn_ptr2: fn(i32) -> i32 = capturing;  // ERROR: captures 'factor'
    println!("   Capturing closures CANNOT be coerced to fn pointers");
    println!("   Only non-capturing closures have a known size at compile time");
    println!("   capturing(10) = {}", capturing(10));
}
