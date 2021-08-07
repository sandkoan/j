import j.Env
import j.eval
import j.lex

fun main() {
    println("Welcome to the J prompt.")
    System.out.flush()

    print("j) ")

    var line = readLine()
    val env = Env()

    while (true) {
        run {
            val tokens = lex(line!!).getOrThrow()
            eval(tokens, env).mapCatching { println(it) }.getOrThrow()
        }

        print("j) ")
        line = readLine()
    }
}