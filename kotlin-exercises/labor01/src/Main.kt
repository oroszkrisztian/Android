import kotlin.random.Random

fun isPrime(number: Int): Boolean {
    if (number <= 1) {
        return false
    }
    for (i in 2 until number) {
        if (number % i == 0) {
            return false
        }
    }
    return true
}

fun evenNumbers(number: Int) = number % 2 == 0

fun main() {

    println("Fel 1")
    val num1 = 2
    val num2 = 4
    println("$num1 + $num2 = ${num1 + num2}")

    println("\nFel 2")
    val daysOfWeek = listOf("Monday", "Tuesday", "wednesday", "Thursday", "friday", "Saturday", "Sunday")
    daysOfWeek.forEach { d -> println("\t$d") }
    println("T start")
    daysOfWeek
        .filter { it.startsWith("T") }
        .forEach { println("\t$it") }
    println("contains e")
    daysOfWeek
        .filter { it.contains("e") }
        .forEach { println("\t$it") }
    println("6 letter length")
    daysOfWeek
        .filter { it.length == 6 }
        .forEach { println("\t$it") }

    println("\nFel 3")
    val start = 10
    val end = 50
    println("Prime numbers between $start and $end are:")
    (start..end).forEach {
        if (isPrime(number = it)) {
            println(it)
        }
    }

    println("\nFel 4")

    println("\nFel 5")
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
    numbers.filter { evenNumbers(it) }.forEach { println(it) }

    println("\nFel 6")
    println("\tDouble the elements of a list of integers and print it")
    val doubledNumbers = numbers.map { it * 2 }
    println("\tDoubled numbers: $doubledNumbers")

    println("\tPrint the days of week capitalized (e.g. MONDAY for Monday)")
    val capitalized = daysOfWeek.map { it.uppercase() }
    println("\tdays of week capitalized: $capitalized")

    println("\tPrint the first character of each day capitalized (e.g. m for Monday)")
    daysOfWeek.map { it.replaceFirstChar { ch -> ch.uppercase() } }.forEach { println(it) }

    println("\tPrint the length of each day (e.g. Monday -> 6)")
    val dayLengths = daysOfWeek.map { it.length }
    println("\tLengths of days: $dayLengths")

    println("\tCompute the average length of days (in number of characters)")
    val averageLength = dayLengths.average()
    println("\tAverage length of days: $averageLength")

    //7

    val mutableDays = daysOfWeek.toMutableList()


    mutableDays.removeIf { it.contains("n", ignoreCase = true) }
    println("Days of week after removing days with 'n': $mutableDays")


    println("\nList with index:")
    mutableDays.withIndex().forEach { (index, day) ->
        println("Item at $index is $day")
    }


    mutableDays.sort()
    println("\nSorted list alphabetically: $mutableDays")
    // 8

    val randomArray = Array(10) { Random.nextInt(0, 101) }


    println("Random array elements:")
    randomArray.forEach { println(it) }


    val sortedArray = randomArray.sortedArray()
    println("\nSorted array:")
    sortedArray.forEach { println(it) }


    val hasEvenNumber = randomArray.any { it % 2 == 0 }
    println("\nArray contains even number: $hasEvenNumber")


    val allEven = randomArray.all { it % 2 == 0 }
    println("All numbers are even: $allEven")


    val average = randomArray.average()


    println("\nAverage of the array:")
    listOf(average).forEach { println(it) }
}
