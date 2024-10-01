package extension

fun String.monogram() : String{
    return  this
        .split(" ")
        .map { it.first().uppercase() }
        .joinToString ("" )
}