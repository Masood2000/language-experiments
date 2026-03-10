# Experiment: Python's garbage collector and reference counting
# Python uses reference counting + a cyclic GC. Let's see both in action.

import sys
import gc

print("=== Reference Counting ===")

a = [1, 2, 3]
print(f"sys.getrefcount(a): {sys.getrefcount(a)}")
# Note: getrefcount itself adds a temporary reference, so it's always +1

b = a
print(f"After b = a: {sys.getrefcount(a)}")

c = [a, a, a]
print(f"After c = [a, a, a]: {sys.getrefcount(a)}")

del b
print(f"After del b: {sys.getrefcount(a)}")

del c
print(f"After del c: {sys.getrefcount(a)}")

print("\n=== Circular References ===")

# Reference counting can't handle cycles
class Node:
    def __init__(self, name):
        self.name = name
        self.other = None
    def __del__(self):
        print(f"  Node '{self.name}' being collected")

# Create a cycle
gc.disable()  # Disable cyclic GC to show the problem

a = Node("A")
b = Node("B")
a.other = b
b.other = a  # Cycle!

# Delete references — but objects still reference each other
del a
del b
print("After del a, del b (GC disabled): nothing collected yet")

# Now enable GC and collect
gc.enable()
collected = gc.collect()
print(f"gc.collect() freed {collected} objects")

print("\n=== Weak References — breaking cycles ===")

import weakref

class Cache:
    def __init__(self, name):
        self.name = name

obj = Cache("important")
weak = weakref.ref(obj)

print(f"Weak ref alive: {weak()}")
del obj
print(f"After del obj: {weak()}")  # None — doesn't prevent collection

print("\n=== GC Generations ===")

print(f"GC thresholds: {gc.get_threshold()}")
print(f"GC counts (gen0, gen1, gen2): {gc.get_count()}")
# Objects that survive collections get promoted to older generations
# Older generations are collected less frequently
