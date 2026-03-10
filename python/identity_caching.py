# Experiment: Python caches small integers and short strings
# Python pre-allocates integer objects for -5 to 256 and interns short strings.
# This means `is` (identity check) behaves unexpectedly.

print("=== Integer Identity Caching ===")

a = 256
b = 256
print(f"a = 256, b = 256")
print(f"a is b: {a is b}")  # True — same cached object

a = 257
b = 257
print(f"\na = 257, b = 257")
print(f"a is b: {a is b}")  # False in REPL, may be True in script (compiler optimization)

# To prove it, check ids
a = 100
b = 100
print(f"\nid(100) called twice: {id(a)} == {id(b)} → {id(a) == id(b)}")

a = 1000
b = 1000
print(f"id(1000) called twice: {id(a)} == {id(b)} → {id(a) == id(b)}")

print("\n=== String Interning ===")

s1 = "hello"
s2 = "hello"
print(f"'hello' is 'hello': {s1 is s2}")  # True — interned

s1 = "hello world!"
s2 = "hello world!"
print(f"'hello world!' is 'hello world!': {s1 is s2}")  # May vary — not always interned

# Strings that look like identifiers get interned
s1 = "hello_world"
s2 = "hello_world"
print(f"'hello_world' is 'hello_world': {s1 is s2}")  # True — looks like identifier
