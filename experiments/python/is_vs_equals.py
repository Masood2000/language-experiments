# Experiment: `is` vs `==` — identity vs equality
# Python has two comparison mechanisms that behave very differently.

print("=== Tuple comparison trap ===")

a = (1, 2, 3)
b = (1, 2, 3)
print(f"a == b: {a == b}")    # True — same values
print(f"a is b: {a is b}")    # May be True (compiler optimization) or False

# But with mutation-proof evidence:
a = tuple(range(3))
b = tuple(range(3))
print(f"\ntuple(range(3)) == tuple(range(3)): {a == b}")
print(f"tuple(range(3)) is tuple(range(3)): {a is b}")

print("\n=== Boolean is integer ===")

print(f"True == 1: {True == 1}")
print(f"True is 1: {True is 1}")
print(f"False == 0: {False == 0}")
print(f"isinstance(True, int): {isinstance(True, int)}")

# This leads to surprising behavior:
print(f"\n{True + True + True = }")  # 3
print(f"{['zero', 'one'][True] = }")  # 'one'

print("\n=== None, True, False are singletons ===")
print(f"None is None: {None is None}")  # Always True
# That's why we use `is None` not `== None`

print("\n=== NaN is not equal to itself ===")
nan = float('nan')
print(f"nan == nan: {nan == nan}")      # False!
print(f"nan is nan: {nan is nan}")      # True — same object
print(f"nan != nan: {nan != nan}")      # True!
