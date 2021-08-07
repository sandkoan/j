package j


typealias Env = HashMap<String, Array>
typealias ParseError = Exception

fun eval(tokens: List<Token>, env: Env = Env()): Result<Array> {
    if (tokens.size >= 2) {
        val head = tokens.first()
        val tail = tokens.drop(1)

        val a = when (head) {
            is Token.Variable -> {
                if (tail.first() == Token.Verb('=') && tail.size >= 2) {
                    val expr = tail.drop(1)

                    val x = eval(expr, env).getOrThrow()
                    env[head.toString()] = x
                    return Result.success(x)
                }
                env.getOrPut(head.toString()) { arrayFromLong(0) }
                //.copy()
            }
            is Token.Number -> arrayFromLong(head.n)
            is Token.Verb -> arrayFromLong(0)
        }

        return if (head is Token.Verb) {
            val x = eval(tail, env).getOrThrow()
            Result.success(
                when (val verb = head.c) {
                    '+' -> id(x)
                    '{' -> size(x)
                    '~' -> iota(x)
                    '<' -> boxing(x)
                    '#' -> sha(x)
                    else -> return Result.failure(ParseError("Unknown monadic verb `$verb`"))
                }
            )
        } else if (tail.first() is Token.Verb && tail.size >= 2) {
            val verb = tail.first() as Token.Verb
            val expr = tail.drop(1)

            val b = eval(expr, env).getOrThrow()
            Result.success(
                when (val v = verb.c) {
                    '+' -> plus(a, b)
                    '{' -> from(a, b)
                    '#' -> rsh(a, b)
                    ',' -> cat(a, b)
                    else -> return Result.failure(ParseError("Unknown dyadic verb `$v`"))
                }
            )
        } else {
            Result.success(a)
        }
    } else {
        return Result.success(arrayFromLong(0))
    }

}

fun main() {
    println(eval(lex("1").getOrThrow()))
    println(eval(lex("5#3,4").getOrThrow()))
    println(eval(lex("shp=2,3").getOrThrow()))
    println(eval(lex("shp#~10").getOrThrow()))
}
