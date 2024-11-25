package compose.common.pattern

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import androidx.annotation.IntRange

@Immutable
data class Dot(
    val cell: Cell,
    val offset: Offset,
)

@Immutable
data class Cell(
    @IntRange(from = 0)
    val row: Int,
    @IntRange(from = 0)
    val column: Int,
)