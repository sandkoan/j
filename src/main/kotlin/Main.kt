import j.Env
import j.eval
import j.lex

fun main() {
    println("Welcome to the J prompt.")
    System.out.flush()

    var line = readLine()
    val env = Env()
    while (true) {
        print("j) ")
        run {
            val tokens = lex(line!!).getOrThrow()
            eval(tokens, env).mapCatching { println(it) }.getOrThrow()
        }

        line = readLine()
    }
}