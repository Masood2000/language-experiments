import java.util.*;

/**
 * MutableHashMapKey.java
 *
 * Demonstrates that mutating an object used as a HashMap key makes the
 * entry unretrievable -- it becomes a "ghost" entry visible only by
 * iteration. Also shows how breaking the equals/hashCode contract
 * creates duplicate keys.
 */
public class MutableHashMapKey {

    static class MutableKey {
        int id;

        MutableKey(int id) { this.id = id; }

        @Override
        public int hashCode() { return id; }

        @Override
        public boolean equals(Object o) {
            return o instanceof MutableKey && ((MutableKey) o).id == this.id;
        }

        @Override
        public String toString() { return "Key(" + id + ")"; }
    }

    // Broken: equals without hashCode
    static class BrokenKey {
        int value;

        BrokenKey(int value) { this.value = value; }

        @Override
        public boolean equals(Object o) {
            return o instanceof BrokenKey && ((BrokenKey) o).value == this.value;
        }

        // Deliberately NOT overriding hashCode -- breaks the contract!

        @Override
        public String toString() { return "BrokenKey(" + value + ")"; }
    }

    public static void main(String[] args) {
        System.out.println("=== Mutable HashMap Key Gotchas ===\n");

        // Experiment 1: Mutating a key makes the entry unretrievable
        System.out.println("1. Mutating a key creates a 'ghost' entry:");
        Map<MutableKey, String> map = new HashMap<>();
        MutableKey key = new MutableKey(1);
        map.put(key, "original value");
        System.out.println("   Before mutation: map.get(key) = " + map.get(key));

        key.id = 2;  // Mutate the key!
        System.out.println("   After key.id = 2: map.get(key) = " + map.get(key));
        System.out.println("   map.size() = " + map.size() + " (entry still exists!)");
        System.out.println("   map.containsKey(key) = " + map.containsKey(key));

        // Can't even find it with a new key matching old hash
        MutableKey searchKey = new MutableKey(1);
        System.out.println("   map.get(new Key(1)) = " + map.get(searchKey));
        System.out.println("   The entry is stuck in the wrong bucket -- unreachable!");

        System.out.println();

        // Experiment 2: Ghost entry is visible via iteration
        System.out.println("2. Ghost entry visible only through iteration:");
        for (Map.Entry<MutableKey, String> entry : map.entrySet()) {
            System.out.println("   Found: " + entry.getKey() + " -> " + entry.getValue());
        }
        System.out.println("   Iteration finds it, but get/containsKey cannot!");

        System.out.println();

        // Experiment 3: Breaking equals/hashCode contract
        System.out.println("3. Breaking equals/hashCode contract:");
        Map<BrokenKey, String> broken = new HashMap<>();
        BrokenKey k1 = new BrokenKey(1);
        BrokenKey k2 = new BrokenKey(1);
        System.out.println("   k1.equals(k2) = " + k1.equals(k2));
        System.out.println("   k1.hashCode() = " + k1.hashCode() + ", k2.hashCode() = " + k2.hashCode());

        broken.put(k1, "first");
        broken.put(k2, "second");
        System.out.println("   map.size() = " + broken.size());
        System.out.println("   Two 'equal' keys coexist as separate entries!");
        for (Map.Entry<BrokenKey, String> entry : broken.entrySet()) {
            System.out.println("   " + entry.getKey() + " -> " + entry.getValue());
        }

        System.out.println();

        // Experiment 4: Correct behavior with immutable keys
        System.out.println("4. String keys (immutable) work correctly:");
        Map<String, String> safe = new HashMap<>();
        String s1 = new String("key");
        String s2 = new String("key");
        safe.put(s1, "first");
        safe.put(s2, "second");
        System.out.println("   s1 == s2: " + (s1 == s2) + " (different objects)");
        System.out.println("   s1.equals(s2): " + s1.equals(s2));
        System.out.println("   map.size() = " + safe.size() + " (correctly merged)");
        System.out.println("   Immutable keys with correct hashCode are safe!");

        System.out.println();

        // Experiment 5: IdentityHashMap uses == instead of equals
        System.out.println("5. IdentityHashMap uses == not equals():");
        IdentityHashMap<String, String> identity = new IdentityHashMap<>();
        identity.put(s1, "first");
        identity.put(s2, "second");
        System.out.println("   IdentityHashMap.size() = " + identity.size());
        System.out.println("   Same 'equal' keys are kept separate (uses == not equals)!");
    }
}
