// Experiment: Kotlin coroutines are NOT threads — they're lightweight and cooperative
// NOTE: Requires kotlinx-coroutines library to run. See INSIGHTS.md for results.
// Run with: kotlinc -cp kotlinx-coroutines-core-1.8.1.jar -script coroutine_vs_thread.kts
//
// Key concepts demonstrated here (with expected output):
//
// 1. You can launch 100,000 coroutines — each uses only ~few hundred bytes
//    (100,000 threads would need ~100GB of stack memory and crash with OOM)
//
// 2. Coroutines are cooperative: they suspend at explicit points (delay, yield)
//    Threads are preemptive: the OS can interrupt them at any time
//
// 3. Structured concurrency: parent coroutine waits for all children
//    Cancelling a parent cancels ALL children automatically
//
// 4. Dispatchers control which thread pool runs the coroutine:
//    - Dispatchers.Default: CPU-bound work (thread pool = number of cores)
//    - Dispatchers.IO: blocking I/O (larger thread pool)
//    - Dispatchers.Main: UI thread (Android/Desktop)

import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun main() = runBlocking {
    println("=== 100,000 coroutines ===")
    val time = measureTimeMillis {
        val jobs = List(100_000) {
            launch {
                delay(1000L)  // Suspend, don't block
            }
        }
        jobs.forEach { it.join() }
    }
    println("100,000 coroutines with 1s delay: ${time}ms")

    println("\n=== Structured concurrency ===")
    val result = coroutineScope {
        val a = async { delay(100); "Hello" }
        val b = async { delay(200); "World" }
        "${a.await()} ${b.await()}"
    }
    println("Combined: $result")

    println("\n=== Cancellation propagation ===")
    val parent = launch {
        launch {
            try { delay(Long.MAX_VALUE) }
            finally { println("  Child cleaned up") }
        }
        delay(100)
        cancel()
    }
    parent.join()
    println("Parent and children done")
}
