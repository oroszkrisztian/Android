package main.repositories

import main.Item

import java.io.File

class ItemRepository {

    private val items = mutableListOf<Item>()

    init {
        loadItemsFromFile("input.txt")
    }

    private fun loadItemsFromFile(fileName: String) {
        val file = File(fileName)
        file.forEachLine { line ->
            val parts = line.split("|")
            if (parts.size == 5) {
                val question = parts[0]
                val answers = parts.slice(1..3)
                val correct = parts[4].toInt()
                items.add(Item(question, answers, correct))
            }
        }
    }

    fun randomItem(): Item {
        return items.random()
    }

    fun selectRandomItems(count: Int): List<Item> {
        return items.shuffled().take(count)
    }

    fun save(item: Item) {
        items.add(item)
    }

    fun size(): Int {
        return items.size
    }
}
