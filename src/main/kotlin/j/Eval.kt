package j


typealias Env = HashMap<String, Array>
typealias ParseError = Exception

fun eval(tokens: List<Token>, env: Env = Env()): Result<Array> {
    if (tokens.isNotEmpty()) {
        val head = tokens.first()
        val tail = tokens.drop(1)

        val a = when (head) {
            is Token.Variable -> {
                if (tail.isNotEmpty() && tail.first() == Token.Verb('=')) {
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
        } else if (tail.isNotEmpty() && tail.first() is Token.Verb) {
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
    val env = Env()
    // Atoms
    println(eval(lex("").getOrThrow(), env))
    println(eval(lex("1").getOrThrow(), env))
    println(eval(lex("123").getOrThrow(), env))
    println(eval(lex("abc").getOrThrow(), env))
    // Monads
    println(eval(lex("+10").getOrThrow(), env))
    println(eval(lex("{10").getOrThrow(), env))
    println(eval(lex("<10").getOrThrow(), env))
    println(eval(lex("~10").getOrThrow(), env))
    println(eval(lex("#10").getOrThrow(), env))
    // Dyads
    println(eval(lex("1+2").getOrThrow(), env))
    println(eval(lex("1,2,3").getOrThrow(), env))
    println(eval(lex("1{5,7,9").getOrThrow(), env))
    println(eval(lex("5#3,4").getOrThrow(), env))
    println(eval(lex("shp=2,3").getOrThrow(), env))
    println(eval(lex("shp#~10").getOrThrow(), env))
    // Variables
    println(eval(lex("a=3").getOrThrow(), env))
    println(eval(lex("b=4").getOrThrow(), env))
    println(eval(lex("d=1+c=a+b").getOrThrow(), env))
    println(eval(lex("d+c").getOrThrow(), env))
}
