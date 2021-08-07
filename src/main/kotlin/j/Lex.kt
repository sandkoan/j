package j

sealed class Token {
    data class Number(val n: Long) : Token()
    data class Variable(val s: String) : Token()
    data class Verb(val c: Char) : Token()
}

typealias LexError = Exception

fun lex(s: String): Result<List<Token>> {
    val result = arrayListOf<Token>()

    var idx = 0
    while (idx < s.length) {
        result.add(
            when (val c = s[idx]) {
                in '0'..'9' -> {
                    val x = s.drop(idx).takeWhile { it.isDigit() }
                    idx += x.length
                    Token.Number(x.toLong())
                }
                in 'a'..'z' -> {
                    val x = s.drop(idx).takeWhile { it.isLetter() }
                    idx += x.length
                    Token.Variable(x)
                }
                '+', '{', '~', '<', '#', ',', '=' -> {
                    idx += 1
                    Token.Verb(c)
                }
                else -> return Result.failure(LexError("Unexpected `$c`"))
            }
        )
    }

    return Result.success(result)
}

fun main() {
    println(lex(""))
    println(lex("5#3,4"))
    println(lex("sh&&&p=2,3"))
    println(lex("shp#~10"))
}