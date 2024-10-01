package dictionary

class DictionaryProvider {
    companion object {
        fun createDictionary(type: DictionaryType) : IDictionary{
            return when (type){
                DictionaryType.ARRAY_LIST -> ListDictionary;
                DictionaryType.HASH_SET-> ListDictionary;
                DictionaryType.TREE_SET-> ListDictionary;
            }
        }
    }
}