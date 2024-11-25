package compose.common.pattern

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset

@Immutable
data class Line(
    val start: Offset,
    val end: Offset
) {
    companion object {
        val Unspecified = Line(Offset.Unspecified, Offset.Unspecified)
    }
}