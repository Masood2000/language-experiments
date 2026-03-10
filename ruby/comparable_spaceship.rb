# comparable_spaceship.rb
#
# Demonstrates Ruby's spaceship operator <=>, the Comparable mixin,
# and surprising behaviors in sorting and comparison.

puts "=== Comparable & Spaceship Operator ==="
puts

# Experiment 1: The spaceship operator returns -1, 0, 1, or nil
puts "1. Spaceship operator (<=>) basics:"
puts "   1 <=> 2 = #{1 <=> 2}"
puts "   2 <=> 2 = #{2 <=> 2}"
puts "   3 <=> 2 = #{3 <=> 2}"
puts "   1 <=> 'a' = #{(1 <=> 'a').inspect} (nil for incomparable types!)"
puts "   'a' <=> 'b' = #{'a' <=> 'b'}"

puts

# Experiment 2: nil from <=> causes sort to crash
puts "2. nil from <=> crashes sort:"
begin
  [1, "two", 3].sort
rescue => e
  puts "   [1, 'two', 3].sort raises: #{e.class}"
  puts "   #{e.message}"
end

puts

# Experiment 3: Comparable mixin gives you 6 methods for free
puts "3. Comparable mixin -- implement <=> get 6 methods free:"
class Temperature
  include Comparable
  attr_reader :degrees

  def initialize(degrees)
    @degrees = degrees
  end

  def <=>(other)
    @degrees <=> other.degrees
  end

  def to_s
    "#{@degrees}°"
  end
end

hot = Temperature.new(100)
cold = Temperature.new(0)
warm = Temperature.new(37)

puts "   hot > cold: #{hot > cold}"
puts "   cold < warm: #{cold < warm}"
puts "   warm.between?(cold, hot): #{warm.between?(cold, hot)}"
puts "   warm.clamp(cold, Temperature.new(36)): #{warm.clamp(cold, Temperature.new(36))}"
puts "   [hot, cold, warm].sort: #{[hot, cold, warm].sort.map(&:to_s)}"
puts "   All from just implementing <=> !"

puts

# Experiment 4: == from Comparable uses <=>
puts "4. Comparable's == uses <=>:"
t1 = Temperature.new(100)
t2 = Temperature.new(100)
puts "   t1.equal?(t2) = #{t1.equal?(t2)} (different objects)"
puts "   t1 == t2 = #{t1 == t2} (Comparable uses <=> for ==)"
puts "   t1.eql?(t2) = #{t1.eql?(t2)} (eql? is NOT overridden!)"
puts "   == and eql? can disagree when using Comparable!"

puts

# Experiment 5: Sorting stability and custom comparisons
puts "5. sort_by vs sort with <=>:"
words = %w[banana cherry apple date elderberry]
puts "   By length: #{words.sort_by(&:length)}"
puts "   By last char: #{words.sort_by { |w| w[-1] }}"
# sort_by is faster for expensive comparisons (Schwartzian transform)
puts "   sort_by uses Schwartzian transform (compute once, sort, extract)"

puts

# Experiment 6: Ranges use <=> for inclusion
puts "6. Ranges use <=> for inclusion checks:"
puts "   (0°..100°) === 37°: #{(Temperature.new(0)..Temperature.new(100)) === warm}"
puts "   Ranges with === work with any Comparable object!"

puts

# Experiment 7: Enumerable min/max use <=>
puts "7. min/max/minmax from Comparable:"
temps = [Temperature.new(20), Temperature.new(35), Temperature.new(5)]
puts "   min = #{temps.min}"
puts "   max = #{temps.max}"
puts "   minmax = #{temps.minmax.map(&:to_s)}"
puts "   All comparison-based methods work automatically!"
