# Experiment: Closures in Python use late binding
# The closure variable is looked up at call time, not definition time.
# This trips up even experienced developers.

print("=== Late Binding Closures ===")

functions = []
for i in range(5):
    functions.append(lambda: i)

print("Expected: 0, 1, 2, 3, 4")
print(f"Actual:   {', '.join(str(f()) for f in functions)}")
# All print 4! Because `i` is looked up when the lambda is called,
# and by then the loop is done and i == 4.

print("\n=== Fix 1: Default argument capture ===")
functions = []
for i in range(5):
    functions.append(lambda i=i: i)  # captures current value of i

print(f"Fixed:    {', '.join(str(f()) for f in functions)}")

print("\n=== Fix 2: functools.partial ===")
from functools import partial

def return_value(x):
    return x

functions = [partial(return_value, i) for i in range(5)]
print(f"Partial:  {', '.join(str(f()) for f in functions)}")

print("\n=== This also affects list comprehensions creating closures ===")
funcs = [lambda: x for x in range(3)]
print(f"Comprehension closures: {[f() for f in funcs]}")  # [2, 2, 2]
