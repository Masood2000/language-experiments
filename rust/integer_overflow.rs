/// integer_overflow.rs
///
/// Demonstrates how Rust handles integer overflow differently in debug vs
/// release mode, provides explicit wrapping/saturating/checked arithmetic,
/// and how `as` casts silently truncate.

fn main() {
    println!("=== Integer Overflow in Rust ===\n");

    // Experiment 1: In debug mode, overflow panics
    println!("1. Debug mode: overflow panics at runtime:");
    let a: u8 = 255;
    println!("   u8 max = {}", a);
    // In debug mode: a + 1 would panic!
    // In release mode: a + 1 wraps to 0
    println!("   a + 1 in debug mode would PANIC (caught below)");

    // Can't even compile `255u8 + 1` as a constant -- compiler catches it!
    // At runtime with non-constant values:
    let result = std::panic::catch_unwind(|| {
        let b: u8 = std::hint::black_box(255);
        b + 1  // Panics in debug mode!
    });
    match result {
        Ok(val) => println!("   Result: {} (release mode: wraps silently)", val),
        Err(_) => println!("   PANICKED! (debug mode catches overflow)"),
    }

    println!();

    // Experiment 2: Wrapping arithmetic (intentional wrapping)
    println!("2. Wrapping arithmetic (explicit, no panic):");
    let x: u8 = 255;
    println!("   255u8.wrapping_add(1) = {}", x.wrapping_add(1));
    println!("   255u8.wrapping_add(10) = {}", x.wrapping_add(10));
    println!("   0u8.wrapping_sub(1) = {}", 0u8.wrapping_sub(1));
    println!("   100u8.wrapping_mul(3) = {}", 100u8.wrapping_mul(3));

    println!();

    // Experiment 3: Saturating arithmetic (clamps to min/max)
    println!("3. Saturating arithmetic (clamps at boundaries):");
    println!("   255u8.saturating_add(1) = {}", 255u8.saturating_add(1));
    println!("   255u8.saturating_add(100) = {}", 255u8.saturating_add(100));
    println!("   0u8.saturating_sub(1) = {}", 0u8.saturating_sub(1));
    println!("   0i8.saturating_sub(100) = {}", 0i8.saturating_sub(100));

    println!();

    // Experiment 4: Checked arithmetic (returns Option)
    println!("4. Checked arithmetic (returns None on overflow):");
    println!("   255u8.checked_add(1) = {:?}", 255u8.checked_add(1));
    println!("   200u8.checked_add(50) = {:?}", 200u8.checked_add(50));
    println!("   100u8.checked_mul(2) = {:?}", 100u8.checked_mul(2));
    println!("   100u8.checked_mul(3) = {:?}", 100u8.checked_mul(3));

    println!();

    // Experiment 5: Overflowing arithmetic (returns value + bool)
    println!("5. Overflowing arithmetic (returns (result, overflowed)):");
    println!("   255u8.overflowing_add(1) = {:?}", 255u8.overflowing_add(1));
    println!("   200u8.overflowing_add(50) = {:?}", 200u8.overflowing_add(50));
    println!("   128i8.overflowing_neg() = {:?}", (-128i8).overflowing_neg());

    println!();

    // Experiment 6: 'as' casts silently truncate
    println!("6. 'as' casts silently truncate:");
    let big: u16 = 256;
    let truncated: u8 = big as u8;
    println!("   256u16 as u8 = {} (truncated to lower 8 bits!)", truncated);

    let negative: i16 = -1;
    let as_unsigned: u16 = negative as u16;
    println!("   -1i16 as u16 = {} (reinterpreted bits)", as_unsigned);

    let large: u32 = 300;
    let small: u8 = large as u8;
    println!("   300u32 as u8 = {} (300 mod 256 = 44)", small);

    println!();

    // Experiment 7: Float to int truncation
    println!("7. Float-to-integer 'as' cast behavior:");
    println!("   3.9f64 as i32 = {} (truncates, doesn't round)", 3.9f64 as i32);
    println!("   -2.7f64 as i32 = {} (truncates toward zero)", -2.7f64 as i32);
    println!("   f64::NAN as i32 = {} (NaN becomes 0!)", f64::NAN as i32);
    println!("   f64::INFINITY as i32 = {} (saturates to MAX)", f64::INFINITY as i32);
    println!("   f64::NEG_INFINITY as i32 = {} (saturates to MIN)", f64::NEG_INFINITY as i32);
}
