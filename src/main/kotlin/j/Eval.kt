package j


typealias Env = HashMap<String, Array>
typealias ParseError = Exception

fun eval(tokens: List<Token>, env: Env): Result<Array> {
    val head = tokens.first()
    val tail = tokens.drop(1)
    val a = when (head) {
        is Token.Variable -> {
            TODO()
        }
        is Token.Number -> arrayFromLong(head.n)
        is Token.Verb -> arrayFromLong(0)
    }
    TODO()
}