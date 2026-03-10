/**
 * SuppressedExceptions.java
 *
 * Demonstrates how try-with-resources preserves both the primary exception
 * and close() exceptions via the suppressed mechanism, while plain
 * try-finally silently loses the original exception.
 */
public class SuppressedExceptions {

    // A resource that throws on close
    static class FaultyResource implements AutoCloseable {
        private final String name;

        FaultyResource(String name) {
            this.name = name;
            System.out.println("   Opened: " + name);
        }

        void doWork() throws Exception {
            throw new Exception(name + ": work failed!");
        }

        @Override
        public void close() throws Exception {
            throw new Exception(name + ": close failed!");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Suppressed Exceptions ===\n");

        // Experiment 1: Plain try-finally LOSES the original exception
        System.out.println("1. Plain try-finally (exception lost):");
        try {
            FaultyResource r = new FaultyResource("resource1");
            try {
                r.doWork();  // Throws "work failed"
            } finally {
                r.close();   // Throws "close failed" -- REPLACES work failed!
            }
        } catch (Exception e) {
            System.out.println("   Caught: " + e.getMessage());
            System.out.println("   Suppressed count: " + e.getSuppressed().length);
            System.out.println("   The 'work failed' exception is LOST forever!");
        }

        System.out.println();

        // Experiment 2: try-with-resources PRESERVES both
        System.out.println("2. Try-with-resources (both exceptions preserved):");
        try {
            try (FaultyResource r = new FaultyResource("resource2")) {
                r.doWork();  // Throws "work failed"
                // close() also throws, but it becomes a SUPPRESSED exception
            }
        } catch (Exception e) {
            System.out.println("   Primary: " + e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("   Suppressed: " + suppressed.getMessage());
            }
            System.out.println("   Both exceptions are accessible!");
        }

        System.out.println();

        // Experiment 3: Multiple resources -- close in reverse order
        System.out.println("3. Multiple resources close in reverse order:");
        try {
            try (FaultyResource r1 = new FaultyResource("first");
                 FaultyResource r2 = new FaultyResource("second");
                 FaultyResource r3 = new FaultyResource("third")) {
                throw new Exception("main operation failed");
            }
        } catch (Exception e) {
            System.out.println("   Primary: " + e.getMessage());
            for (Throwable suppressed : e.getSuppressed()) {
                System.out.println("   Suppressed: " + suppressed.getMessage());
            }
            System.out.println("   Resources closed in reverse order (third, second, first)");
        }

        System.out.println();

        // Experiment 4: Only close() throws -- it becomes the primary
        System.out.println("4. Only close() throws (no primary exception):");
        try {
            try (FaultyResource r = new FaultyResource("resource4")) {
                System.out.println("   Work completed successfully");
                // No exception during work -- close() exception becomes primary
            }
        } catch (Exception e) {
            System.out.println("   Caught: " + e.getMessage());
            System.out.println("   When there's no primary exception, close() exception IS the primary");
        }

        System.out.println();

        // Experiment 5: Manually adding suppressed exceptions
        System.out.println("5. Manually adding suppressed exceptions:");
        Exception primary = new Exception("main error");
        primary.addSuppressed(new Exception("cleanup error 1"));
        primary.addSuppressed(new Exception("cleanup error 2"));
        System.out.println("   Primary: " + primary.getMessage());
        System.out.println("   Suppressed exceptions: " + primary.getSuppressed().length);
        for (Throwable s : primary.getSuppressed()) {
            System.out.println("   - " + s.getMessage());
        }
    }
}
