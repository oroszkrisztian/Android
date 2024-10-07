import dictionary.DictionaryProvider
import dictionary.DictionaryType
import dictionary.IDictionary
import dictionary.ListDictionary
import extension.monogram

fun main(){
//    val dict: IDictionary = DictionaryProvider.createDictionary(DictionaryType.ARRAY_LIST)
//    println("Number of words: ${dict.size()}")
//    var word: String?
//    while(true){
//        print("What to find? ")
//        word = readLine()
//        if( word.equals("quit")){
//            break
//        }
//        println("Result: ${word?.let { dict.find(it) }}")
//    }
    val name = "Orosz Krisztian"
    val initials = name.monogram()
    println(initials)
}
