# Experiment: Mutable default arguments are shared across calls
# One of Python's most famous gotchas. Default mutable arguments are
# created ONCE at function definition time, not per call.

def append_to(item, target=[]):
    target.append(item)
    return target

print("=== Mutable Default Argument ===")
print(f"Call 1: append_to('a') → {append_to('a')}")
print(f"Call 2: append_to('b') → {append_to('b')}")
print(f"Call 3: append_to('c') → {append_to('c')}")
# Expected by beginners: ['a'], ['b'], ['c']
# Actual: ['a'], ['a', 'b'], ['a', 'b', 'c']

print("\n=== Proof: the default is the same object ===")
def show_default(x=[]):
    print(f"  id of default list: {id(x)}")
    x.append(1)
    return x

show_default()
show_default()
show_default()

print("\n=== The fix: use None sentinel ===")
def append_to_fixed(item, target=None):
    if target is None:
        target = []
    target.append(item)
    return target

print(f"Call 1: {append_to_fixed('a')}")
print(f"Call 2: {append_to_fixed('b')}")
print(f"Call 3: {append_to_fixed('c')}")
