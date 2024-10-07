package dictionary

import java.io.File

object ListDictionary : IDictionary{
    private val words : MutableList<String> = mutableListOf<String>()

    init {
        File(IDictionary.DICTIONARY_FILE).forEachLine { words.add(it) }
    }
    override fun add(word: String): Boolean {
        when (word) {
            !in words -> {
                words.add(word)
                return true
            }
            else -> {
                return false
            }
        }
    }

    override fun size() : Int{
        return words.size;
    }

    override fun find(word: String): Boolean {
        return word in words
    }


}