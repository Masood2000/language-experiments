// Experiment: Sealed classes + when expressions — Kotlin's exhaustive pattern matching
// The compiler guarantees you handle all cases. No missed branches.

sealed class NetworkResult {
    data class Success(val data: String) : NetworkResult()
    data class Error(val code: Int, val message: String) : NetworkResult()
    data object Loading : NetworkResult()
}

sealed interface Shape {
    data class Circle(val radius: Double) : Shape
    data class Rectangle(val width: Double, val height: Double) : Shape
    data class Triangle(val base: Double, val height: Double) : Shape
}

// The compiler FORCES you to handle all cases (no else needed)
fun handle(result: NetworkResult): String = when (result) {
    is NetworkResult.Success -> "Got: ${result.data}"
    is NetworkResult.Error -> "Error ${result.code}: ${result.message}"
    NetworkResult.Loading -> "Loading..."
    // If you add a new subclass, this won't compile until you add a branch!
}

fun area(shape: Shape): Double = when (shape) {
    is Shape.Circle -> Math.PI * shape.radius * shape.radius
    is Shape.Rectangle -> shape.width * shape.height
    is Shape.Triangle -> 0.5 * shape.base * shape.height
}

fun describe(obj: Any): String = when (obj) {
    is String -> "String of length ${obj.length}"  // Smart cast to String!
    is Int -> "Integer: ${obj * 2}"                  // Smart cast to Int!
    is List<*> -> "List of size ${obj.size}"         // Smart cast to List!
    else -> "Unknown: $obj"
}

fun main() {
    println("=== Sealed class — restricted hierarchy ===")
    println(handle(NetworkResult.Success("hello")))
    println(handle(NetworkResult.Error(404, "Not Found")))
    println(handle(NetworkResult.Loading))

    println("\n=== when is an expression — returns a value ===")
    val x = 15
    val description = when {
        x < 0 -> "negative"
        x == 0 -> "zero"
        x in 1..10 -> "small"
        x in 11..100 -> "medium"
        else -> "large"
    }
    println("$x is $description")

    println("\n=== when with smart casting ===")
    println(describe("hello"))
    println(describe(42))
    println(describe(listOf(1, 2, 3)))

    println("\n=== Sealed interface (Kotlin 1.5+) — even more flexible ===")
    println("Circle(5) area = ${area(Shape.Circle(5.0))}")
    println("Rectangle(3,4) area = ${area(Shape.Rectangle(3.0, 4.0))}")

    println("\n=== Destructuring in when ===")
    data class Pair2(val first: Int, val second: Int)
    val pair = Pair2(1, 2)
    val (a, b) = pair
    println("Destructured: a=$a, b=$b")

    val grade = 85
    val letter = when (grade) {
        in 90..100 -> "A"
        in 80..89 -> "B"
        in 70..79 -> "C"
        in 60..69 -> "D"
        else -> "F"
    }
    println("Grade $grade = $letter")
}
