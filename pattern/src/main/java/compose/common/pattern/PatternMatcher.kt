package compose.common.pattern

interface PatternMatcher<T> {
    fun match(cells: List<Cell>, pattern: T): Boolean
}

class PatternComparator<T>(
    private val pattern: T
) : PatternMatcher<T> {

    override fun match(cells: List<Cell>, pattern: T): Boolean {
        return this.pattern == pattern
    }

}

class PatternLengthMatcher<T>(
    private val minLength: Int,
    private val maxLength: Int = Int.MAX_VALUE,
) : PatternMatcher<T> {

    override fun match(cells: List<Cell>, pattern: T): Boolean {
        return cells.size in minLength..maxLength
    }

}
