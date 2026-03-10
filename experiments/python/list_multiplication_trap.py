# Experiment: List multiplication creates shallow copies
# Multiplying a list of mutable objects creates references, not copies.

print("=== The Trap ===")

grid = [[0] * 3] * 3
print(f"Grid created with [[0]*3]*3:")
for row in grid:
    print(f"  {row}")

grid[0][0] = 1
print(f"\nAfter grid[0][0] = 1:")
for row in grid:
    print(f"  {row}")
# ALL rows are modified! They're the same list object.

print(f"\nProof — all rows are the same object:")
print(f"  id(grid[0]) == id(grid[1]) == id(grid[2]): "
      f"{id(grid[0]) == id(grid[1]) == id(grid[2])}")

print("\n=== The Fix ===")

grid = [[0] * 3 for _ in range(3)]
grid[0][0] = 1
print(f"Grid created with list comprehension:")
for row in grid:
    print(f"  {row}")

print(f"\nNow they're different objects:")
print(f"  id(grid[0]) == id(grid[1]): {id(grid[0]) == id(grid[1])}")

print("\n=== Note: [0]*3 is fine because ints are immutable ===")
flat = [0] * 3
flat[0] = 99
print(f"[0]*3 then [0]=99: {flat}")  # [99, 0, 0] — works as expected
