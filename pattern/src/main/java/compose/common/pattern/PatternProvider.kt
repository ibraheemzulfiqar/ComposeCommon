package compose.common.pattern

interface PatternProvider<T> {
    fun build(pattern: List<Cell>): T
    fun parse(pattern: T): List<Cell>
}

class StringPatternProvider : PatternProvider<String> {
    override fun build(pattern: List<Cell>): String {
        return pattern.joinToString(separator = ",") { "${it.row}-${it.column}" }
    }

    override fun parse(pattern: String): List<Cell> {
        return pattern.split(",").map {
            val (row, column) = it.split("-").map(String::toInt)
            Cell(row, column)
        }
    }
}

