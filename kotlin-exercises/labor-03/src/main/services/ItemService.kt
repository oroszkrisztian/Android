package main.services

import main.Item
import main.repositories.ItemRepository

class ItemService(private val itemRepository: ItemRepository) {
    fun selectRandomItems(count: Int): List<Item> {
        return itemRepository.selectRandomItems(count)
    }
}
