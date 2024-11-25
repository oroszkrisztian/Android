package main.controllers

import main.services.ItemService

class ItemController(private val itemService: ItemService) {
    fun quiz(count: Int) {
        val items = itemService.selectRandomItems(count)
        var correctAnswers = 0

        for (item in items) {
            println(item.question)
            for (i in item.answers.indices) {
                println("${i + 1}. ${item.answers[i]}")
            }

            // Get user input
            var userAnswer: Int?
            do {
                println("Please select an answer (1-3): ")
                userAnswer = readLine()?.toIntOrNull()
                if (userAnswer == null || userAnswer !in 1..3) {
                    println("Invalid input! Please choose a number between 1 and 3.")
                }
            } while (userAnswer == null || userAnswer !in 1..3)

            if (userAnswer - 1 == item.correct) {
                correctAnswers++
            }
        }

        println("Quiz finished!")
        println("Correct answers: $correctAnswers / ${items.size}")
    }
}