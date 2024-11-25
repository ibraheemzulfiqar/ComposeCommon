package compose.common.pattern

import android.view.MotionEvent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlin.math.abs

class PatternInputHandler(
    private val touchAreaPx: Float,
    private val dots: List<Dot>,
) {

    private val _connectedLines = mutableStateListOf<Line>()
    val connectedLines: List<Line> get() = _connectedLines

    private val _connectedDots = mutableStateListOf<Dot>()
    val connectedDots: List<Dot> get() = _connectedDots

    var currentLine by mutableStateOf(Line.Unspecified)
        private set


    fun onDragStart(position: Offset): Boolean {
        val (ex, ey) = position
        var result = false

        for (dot in dots) {
            if (dot.isInArea(ex, ey)) {
                result = true
                clear()

                _connectedDots.add(dot)
                currentLine = currentLine.copy(start = dot.offset)
            }
        }

        return result
    }

    fun onDrag(position: Offset) {
        val (ex, ey) = position

        currentLine = currentLine.copy(end = Offset(ex, ey))

        for (dot in dots) {
            if (!connectedDots.contains(dot) && dot.isInArea(ex, ey)) {

                val missingDot = findMissingDot(dot)

                if (missingDot != null) {
                    _connectedDots.add(missingDot)
                }

                _connectedLines.add(currentLine.copy(end = dot.offset))
                _connectedDots.add(dot)

                currentLine = currentLine.copy(start = dot.offset)
            }
        }
    }

    fun onDragEnd(): List<Dot> {
        if (_connectedDots.isNotEmpty()) {
            currentLine = Line.Unspecified
        }

        return _connectedDots.toList()
    }

    fun drawOnEvent(
        event: MotionEvent,
        onStart: () -> Unit,
        onFinish: (dots: List<Dot>) -> Unit,
    ): Boolean {
        if (dots.isEmpty()) return false

        val (ex, ey) = event.x to event.y

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                for (dot in dots) {

                    if (dot.isInArea(ex, ey)) {
                        onStart()
                        clear()

                        _connectedDots.add(dot)
                        currentLine = currentLine.copy(start = dot.offset)
                    }
                }
            }

            MotionEvent.ACTION_MOVE -> {
                currentLine = currentLine.copy(end = Offset(ex, ey))

                for (dot in dots) {
                    if (!connectedDots.contains(dot) && dot.isInArea(ex, ey)) {

                        val missingDot = findMissingDot(dot)

                        if (missingDot != null) {
                            _connectedDots.add(missingDot)
                        }

                        _connectedLines.add(currentLine.copy(end = dot.offset))
                        _connectedDots.add(dot)

                        currentLine = currentLine.copy(start = dot.offset)
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                if (_connectedDots.isNotEmpty()) {
                    currentLine = Line.Unspecified
                    onFinish(_connectedDots)
                }
            }

            else -> {
                return false
            }
        }

        return true
    }

    fun clear() {
        _connectedLines.clear()
        _connectedDots.clear()
        currentLine = Line.Unspecified
    }

    private fun Dot.isInArea(x: Float, y: Float): Boolean {
        val (px, py) = offset

        return x in (px - touchAreaPx)..(px + touchAreaPx) &&
                y in (py - touchAreaPx)..(py + touchAreaPx)
    }

    // Copied from https://github.com/aritraroy/PatternLockView/blob/a90b0d4bf0f286bc23e0356a260b6929adebe7de/patternlockview/src/main/java/com/andrognito/patternlockview/PatternLockView.java#L781
    // No idea what's happening tho
    // TODO : should handle multiple missing dots or test with larger cells
    private fun findMissingDot(dot: Dot): Dot? {
        if (connectedDots.isEmpty()) return null

        val lastCell = connectedDots.last().cell
        val cell = dot.cell

        val dRow = cell.row - lastCell.row
        val dColumn = cell.column - lastCell.column

        var fillInRow = lastCell.row
        var fillInColumn = lastCell.column

        if (abs(dRow) == 2 && abs(dColumn) != 1) {
            fillInRow = lastCell.row + (if ((dRow > 0)) 1 else -1)
        }

        if (abs(dColumn) == 2 && abs(dRow) != 1) {
            fillInColumn = lastCell.column + (if ((dColumn > 0)) 1 else -1)
        }
        val fillInCell = Cell(fillInRow, fillInColumn)

        val fillInGapDot = dots.firstOrNull { it.cell == fillInCell }

        if (fillInGapDot != null && !connectedDots.contains(fillInGapDot)) {
            return fillInGapDot
        }

        return null
    }
}