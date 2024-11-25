package compose.common.pattern

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

internal fun getDotsPositionedEvenly(
    cells: Int,
    layoutSize: IntSize,
    dotSizePx: Float,
): List<Dot> {
    if (layoutSize == IntSize.Zero) return emptyList()

    val cellWidth = layoutSize.width.toFloat() / cells
    val cellHeight = layoutSize.height.toFloat() / cells
    val dotList = mutableListOf<Dot>()

    for (column in 0..cells) {
        for (row in 0..cells) {
            val px = if (column == 0) 1 else if (column == cells) -1 else 0
            val py = if (row == 0) 1 else if (row == cells) -1 else 0

            val dotOffset = Offset(
                (cellWidth * column) + (dotSizePx * px),
                (cellHeight * row) + (dotSizePx * py),
            )

            val dot = Dot(
                cell = Cell(row, column),
                offset = dotOffset,
            )

            dotList.add(dot)
        }
    }

    return dotList
}

fun stringPattern() = StringPatternProvider()
fun <T> minPatternOf(l: Int) = PatternLengthMatcher<T>(l)
fun <T> matchPattern(pattern: T) = PatternComparator(pattern)
operator fun <T> PatternMatcher<T>.plus(other: PatternMatcher<T>) = listOf(this, other)