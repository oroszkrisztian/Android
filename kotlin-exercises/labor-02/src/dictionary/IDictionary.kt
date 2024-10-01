package dictionary

interface IDictionary {

    companion object {
        const val DICTIONARY_FILE = "dictionary.txt"
    }


    fun add(word: String):Boolean
    fun size() : Int
    fun find(word: String): Boolean
}