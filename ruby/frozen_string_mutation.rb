# frozen_string_mutation.rb
#
# Demonstrates Ruby's frozen strings, the magic comment that freezes
# all string literals, and surprising behaviors around string mutability.

puts "=== Frozen String Mutation ==="
puts

# Experiment 1: Strings are mutable by default
puts "1. Strings are mutable by default:"
s = "hello"
s << " world"
puts "   'hello' << ' world' = #{s}"
s[0] = 'H'
puts "   s[0] = 'H' => #{s}"
puts "   Unlike Python/Java, Ruby strings are mutable!"

puts

# Experiment 2: freeze makes strings immutable
puts "2. freeze makes strings immutable:"
frozen = "immutable".freeze
puts "   frozen = #{frozen.inspect}"
puts "   frozen.frozen? = #{frozen.frozen?}"
begin
  frozen << " oops"
rescue => e
  puts "   frozen << ' oops' raises: #{e.class}: #{e.message}"
end

puts

# Experiment 3: Frozen string deduplication
puts "3. Frozen strings are deduplicated:"
a = "hello".freeze
b = "hello".freeze
puts "   a = 'hello'.freeze"
puts "   b = 'hello'.freeze"
puts "   a.equal?(b) = #{a.equal?(b)} (same object in memory!)"
c = "hello"
d = "hello"
puts "   c = 'hello' (no freeze)"
puts "   d = 'hello' (no freeze)"
puts "   c.equal?(d) = #{c.equal?(d)} (different objects)"

puts

# Experiment 4: dup unfreezes, clone preserves frozen
puts "4. dup vs clone with frozen strings:"
original = "test".freeze
duped = original.dup
cloned = original.clone
puts "   original.frozen? = #{original.frozen?}"
puts "   original.dup.frozen? = #{duped.frozen?} (dup UNFREEZES!)"
puts "   original.clone.frozen? = #{cloned.frozen?} (clone preserves frozen)"
duped << "!" # This works
puts "   dup allows mutation: #{duped}"

puts

# Experiment 5: Symbols vs frozen strings
puts "5. Symbols vs frozen strings:"
sym = :hello
str = "hello".freeze
puts "   :hello.object_id = #{sym.object_id}"
puts "   :hello.object_id = #{:hello.object_id} (always same)"
puts "   'hello'.freeze.object_id = #{str.object_id}"
puts "   'hello'.freeze.object_id = #{"hello".freeze.object_id} (also same!)"
puts "   sym.to_s.frozen? = #{sym.to_s.frozen?}"
puts "   Symbols and frozen strings are similar but distinct types"

puts

# Experiment 6: String interpolation creates new (mutable) strings
puts "6. Interpolation creates new mutable strings:"
base = "world".freeze
interpolated = "hello #{base}"
puts "   base.frozen? = #{base.frozen?}"
puts "   \"hello \#{base}\".frozen? = #{interpolated.frozen?}"
interpolated << "!"
puts "   Interpolated string is mutable: #{interpolated}"
puts "   Even though the component was frozen!"

puts

# Experiment 7: freeze on other objects
puts "7. freeze works on any object:"
arr = [1, 2, 3].freeze
begin
  arr << 4
rescue => e
  puts "   Frozen array: #{e.class}"
end
hash = {a: 1}.freeze
begin
  hash[:b] = 2
rescue => e
  puts "   Frozen hash: #{e.class}"
end
puts "   But freeze is shallow -- nested objects are still mutable!"
nested = [[1, 2], [3, 4]].freeze
nested[0] << 99
puts "   Frozen array with nested: #{nested.inspect}"
