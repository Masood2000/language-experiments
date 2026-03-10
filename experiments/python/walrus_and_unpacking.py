# Experiment: Walrus operator and advanced unpacking — Python's unique syntax features

print("=== Extended Unpacking (Python 3) ===")

first, *middle, last = [1, 2, 3, 4, 5]
print(f"first, *middle, last = [1,2,3,4,5]")
print(f"first={first}, middle={middle}, last={last}")

# Works with any iterable
a, *b = "hello"
print(f"\na, *b = 'hello' → a='{a}', b={b}")

# Nested unpacking
(a, b), (c, d) = [1, 2], [3, 4]
print(f"\n(a,b), (c,d) = [1,2], [3,4] → a={a}, b={b}, c={c}, d={d}")

print("\n=== Walrus Operator := (Python 3.8+) ===")

# Read and process in one step
data = [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]

# Filter and capture in one expression
if (n := len(data)) > 5:
    print(f"List has {n} elements (captured with :=)")

# Useful in while loops
import io
stream = io.StringIO("line1\nline2\nline3\n")
while (line := stream.readline()):
    print(f"  Read: {line.strip()}")

# In list comprehensions — compute once, use twice
results = [y for x in range(10) if (y := x**2) > 20]
print(f"\nSquares > 20 from range(10): {results}")

print("\n=== Chained Comparisons (unique to Python) ===")

x = 5
print(f"1 < {x} < 10: {1 < x < 10}")          # True — doesn't exist in most languages
print(f"1 < {x} > 3:  {1 < x > 3}")            # True — any chain works
print(f"1 == 1 < 2:   {1 == 1 < 2}")            # True — (1==1) and (1<2)
print(f"1 < 2 < 3 < 4 < 5: {1 < 2 < 3 < 4 < 5}")  # All chained
