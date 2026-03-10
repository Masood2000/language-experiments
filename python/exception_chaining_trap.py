"""
exception_chaining_trap.py

Demonstrates how Python's exception chaining works (implicit __context__,
explicit __cause__ with 'from'), suppression with 'from None', and the
dangerous behavior where 'return' in 'finally' silently swallows exceptions.
"""

print("=== Exception Chaining Traps ===\n")

# Experiment 1: Implicit exception chaining (__context__)
print("1. Implicit chaining -- exception raised while handling another:")
try:
    try:
        raise ValueError("original error")
    except ValueError:
        raise RuntimeError("new error during handling")
except RuntimeError as e:
    print(f"   Caught: {e}")
    print(f"   __context__: {e.__context__}")
    print(f"   __cause__: {e.__cause__}")
    print("   Python automatically links the original exception via __context__")

print()

# Experiment 2: Explicit chaining with 'from'
print("2. Explicit chaining with 'raise ... from ...':")
try:
    try:
        raise ValueError("database connection failed")
    except ValueError as orig:
        raise RuntimeError("service unavailable") from orig
except RuntimeError as e:
    print(f"   Caught: {e}")
    print(f"   __cause__: {e.__cause__}")
    print(f"   __suppress_context__: {e.__suppress_context__}")
    print("   'from' sets __cause__ and __suppress_context__ = True")

print()

# Experiment 3: Suppression with 'from None'
print("3. Suppressing chain with 'from None':")
try:
    try:
        raise ValueError("internal detail")
    except ValueError:
        raise RuntimeError("clean error message") from None
except RuntimeError as e:
    print(f"   Caught: {e}")
    print(f"   __context__: {e.__context__}")
    print(f"   __cause__: {e.__cause__}")
    print(f"   __suppress_context__: {e.__suppress_context__}")
    print("   'from None' hides the original exception from tracebacks")

print()

# Experiment 4: 'return' in 'finally' swallows exceptions
print("4. 'return' in 'finally' silently swallows exceptions:")
def dangerous():
    try:
        raise ValueError("this exception disappears!")
    finally:
        return "finally's return value"  # noqa

result = dangerous()
print(f"   dangerous() returned: '{result}'")
print("   The ValueError was silently swallowed -- no trace of it!")

print()

# Experiment 5: 'return' in 'finally' overrides 'return' in 'try'
print("5. 'return' in 'finally' overrides 'return' in 'try':")
def override_return():
    try:
        return "from try"
    finally:
        return "from finally"  # noqa

print(f"   override_return() = '{override_return()}'")
print("   The 'try' return value is completely lost!")

print()

# Experiment 6: Exception in 'except' replaces original
print("6. Exception in 'except' block replaces original:")
try:
    try:
        raise ValueError("first")
    except ValueError:
        raise TypeError("second")
except TypeError as e:
    print(f"   Caught TypeError: {e}")
    print(f"   Original ValueError accessible via __context__: {e.__context__}")
    print("   Both exceptions are preserved in the chain")
