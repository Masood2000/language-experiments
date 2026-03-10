"""
tuple_hashability_surprise.py

Demonstrates that tuples are NOT always hashable despite being "immutable",
and the famous += paradox where an operation both succeeds AND raises an error.
"""

print("=== Tuple Hashability Surprise ===\n")

# Experiment 1: Tuples containing mutable objects are unhashable
print("1. Tuple with list inside -- hashable?")
t = (1, 2, [3, 4])
print(f"   t = {t}")
try:
    h = hash(t)
    print(f"   hash(t) = {h}")
except TypeError as e:
    print(f"   hash(t) raises: {e}")

print()

# Experiment 2: You CAN mutate the list inside a "immutable" tuple
print("2. Mutating a list inside a tuple:")
t2 = ([1, 2], [3, 4])
print(f"   Before: {t2}")
t2[0].append(99)
print(f"   After t2[0].append(99): {t2}")
print("   The tuple is 'immutable' but its contents changed!")

print()

# Experiment 3: The famous += paradox
print("3. The += paradox:")
t3 = ([1, 2],)
print(f"   t3 = {t3}")
print("   Executing t3[0] += [5, 6]...")
try:
    t3[0] += [5, 6]
except TypeError as e:
    print(f"   Raises: {e}")
print(f"   But t3 is now: {t3}")
print("   It BOTH raised an error AND mutated the list!")

print()

# Experiment 4: Why does += both fail and succeed?
print("4. Explanation via dis module:")
print("   t3[0] += [5, 6] is equivalent to:")
print("   temp = t3[0].__iadd__([5, 6])  # This SUCCEEDS (mutates the list)")
print("   t3[0] = temp                    # This FAILS (tuple assignment)")
print("   The list.extend happens first, then tuple assignment raises TypeError.")

print()

# Experiment 5: Hashable vs unhashable tuples
print("5. Comparing tuple hashability:")
hashable = (1, 2, (3, 4))  # Nested tuple -- all immutable
unhashable = (1, 2, {3, 4})  # Contains a set -- mutable

print(f"   (1, 2, (3, 4)) hashable: ", end="")
try:
    hash(hashable)
    print("Yes")
except TypeError:
    print("No")

print(f"   (1, 2, {{3, 4}}) hashable: ", end="")
try:
    hash(unhashable)
    print("Yes")
except TypeError:
    print("No")

# This means tuples with lists/sets/dicts can't be dict keys or set members
print("   Consequence: unhashable tuples can't be dict keys or set members!")
