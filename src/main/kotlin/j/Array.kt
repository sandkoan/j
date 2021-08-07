package j

fun <T> Iterable<T>.takeCycle(n: Int): List<T> {
    require(n >= 0) { "Requested element count $n is less than zero." }
    if (n == 0) return emptyList()
    if (this is Collection<T>) {
        if (n == 1) return listOf(first())
    }
    var count = 0
    val list = ArrayList<T>(n)

    outer@ while (true) {
        for (item in this) {
            list.add(item)
            if (++count == n)
                break@outer
        }
    }

    return list
}

data class Array(val depth: List<Long>, val data: List<Element>) {
    override fun toString(): String {
        val sb = StringBuilder()

        sb.append(this.depth.joinToString(separator = " ")).append("\n")

        for (e1 in this.data) {
            when (e1) {
                is Element.Array -> sb.append("< ${e1.arr}")
                is Element.Number -> sb.append("${e1.n} ")
            }
        }
        sb.append("\n")

        return sb.toString()
    }
}

fun tr(d: List<Long>): Long = d.reduce { acc, l -> acc * l }

fun boxed(a: Array): Boolean = a.data.any {
    when (it) {
        is Element.Array -> true
        else -> false
    }
}

fun arrayFromLong(n: Long): Array = Array(depth = arrayListOf(), data = arrayListOf(Element.Number(n)))

fun id(a: Array): Array = a

fun size(a: Array): Array = arrayFromLong(if (boxed(a)) a.depth[0] else 1)

fun iota(a: Array): Array = when (val e = a.data[0]) {
    is Element.Number -> Array(depth = arrayListOf(e.n), (0 until e.n).map { Element.Number(it) })
    is Element.Array -> arrayFromLong(0)
}

fun boxing(a: Array): Array = Array(
    depth = listOf(),
    data = listOf(Element.Array(a))
)

fun sha(a: Array): Array = Array(
    depth = listOf(a.depth.size.toLong()),
    data = a.depth.map { Element.Number(it) }
)

fun at(a: Array, i: Long): Long = if (i.toULong() < a.data.size.toULong()) {
    a.data[i.toInt()].toLong()
} else {
    0
}

fun plus(a: Array, b: Array): Array = Array(
    depth = b.depth,
    data = (0 until b.depth.size.toLong()).map {
        Element.Number(at(a, it) + at(b, it))
    }
)

fun from(a: Array, b: Array): Array {
    val n = tr(b.depth.drop(1))
    return Array(
        depth = b.depth.drop(1),
        data = (0 until n).map {
            b.data[((it + n * at(a, 0)).toInt())]
        }
    )
}

fun rsh(a: Array, b: Array): Array {
    val n = if (a.depth.isEmpty()) {
        at(a, 0)
    } else {
        val depth = (0 until a.depth[0]).map { at(a, it) }
        tr(depth)
    }
    return Array(
        depth = a.data.map { it.toLong() },
        data = b.data.takeCycle(n.toInt())
    )
}

fun cat(a: Array, b: Array): Array {
    val an = tr(a.depth)
    val bn = tr(b.depth)
    val n = an + bn
    return Array(
        depth = listOf(n),
        data = (0 until n).map {
            if (it < an) {
                a.data[it.toInt()]
            } else {
                b.data[((it - an).toInt())]
            }
        }
    )
}

fun main() {
    val l = listOf(1, 2, 3, 4, 5)
    l.takeCycle(10).forEach { println(it) }
}
