# Experiment: Python's descriptor protocol — the hidden engine behind @property, classmethod, etc.
# Descriptors are how Python implements attribute access magic.

print("=== How @property actually works ===")

class Temperature:
    def __init__(self, celsius):
        self._celsius = celsius

    @property
    def fahrenheit(self):
        return self._celsius * 9/5 + 32

    @fahrenheit.setter
    def fahrenheit(self, value):
        self._celsius = (value - 32) * 5/9

t = Temperature(100)
print(f"100°C = {t.fahrenheit}°F")
t.fahrenheit = 32
print(f"32°F = {t._celsius}°C")

print("\n=== Building a descriptor from scratch ===")

class Validated:
    """A descriptor that validates values on assignment."""
    def __init__(self, min_val, max_val):
        self.min_val = min_val
        self.max_val = max_val

    def __set_name__(self, owner, name):
        self.name = name  # Automatically captures the attribute name

    def __get__(self, obj, objtype=None):
        if obj is None:
            return self
        return getattr(obj, f'_{self.name}', None)

    def __set__(self, obj, value):
        if not self.min_val <= value <= self.max_val:
            raise ValueError(f"{self.name} must be between {self.min_val} and {self.max_val}")
        setattr(obj, f'_{self.name}', value)

class Player:
    health = Validated(0, 100)
    mana = Validated(0, 50)

    def __init__(self, health, mana):
        self.health = health
        self.mana = mana

p = Player(80, 30)
print(f"Player health={p.health}, mana={p.mana}")

try:
    p.health = 150
except ValueError as e:
    print(f"Validation caught: {e}")

print("\n=== Functions are descriptors! ===")
# This is how methods work — functions implement __get__

class MyClass:
    def greet(self):
        return "hello"

obj = MyClass()
print(f"Accessing via class:    {type(MyClass.__dict__['greet'])}")  # function
print(f"Accessing via instance: {type(obj.greet)}")                  # bound method
# The function's __get__ binds it to the instance automatically!

print(f"\nManual binding: {MyClass.__dict__['greet'].__get__(obj, MyClass)}")
