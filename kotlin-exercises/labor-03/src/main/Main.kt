package main

import main.controllers.ItemController
import main.repositories.ItemRepository
import main.services.ItemService

fun main(args: Array<String>) {
    val repository = ItemRepository()
    val service = ItemService(repository)
    val controller = ItemController(service)

    println("Welcome to the Kotlin quiz!")

    var numQuestions: Int
    do {
        println("How many questions would you like to answer? (Maximum: 10)")
        numQuestions = readLine()?.toIntOrNull() ?: 0
        if (numQuestions > 10) {
            println("Please choose no more than 10 questions.")
        }
    } while (numQuestions > 10)


    controller.quiz(numQuestions)
}