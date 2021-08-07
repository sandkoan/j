package j

sealed class Element {
    data class Array(val arr: j.Array) : Element()
    data class Number(val n: Long) : Element()

    fun toLong(): Long =
        when (this) {
            is Number -> this.n
            else -> 0
        }
}
