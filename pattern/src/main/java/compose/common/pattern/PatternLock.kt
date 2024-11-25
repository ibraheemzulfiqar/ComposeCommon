@file:OptIn(ExperimentalComposeUiApi::class)

package compose.common.pattern

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import androidx.annotation.IntRange
import androidx.compose.material3.MaterialTheme.colorScheme

@Composable
fun PatternLock(
    modifier: Modifier = Modifier,
    @IntRange(from = 1) cellCount: Int = 3,
    dotSize: Dp = 14.dp,
    selectedDotSize: Dp = 24.dp,
    lineStroke: Dp = 3.dp,
    extraTouch: Dp = 32.dp,
    colors: PatternLockColors = PatternLockDefaults.colors(),
    patternMatchers: List<PatternMatcher<String>> = emptyList(),
    patternMatcherResult: ((result: PatternResult<String>) -> Unit)? = null,
    clearPatternDelay: Long = 800L,
    onCleared: (() -> Unit)? = null,
) {
    PatternLock(
        patternProvider = stringPattern(),
        modifier = modifier,
        cellCount = cellCount,
        dotSize = dotSize,
        selectedDotSize = selectedDotSize,
        lineStroke = lineStroke,
        extraTouch = extraTouch,
        colors = colors,
        patternMatchers = patternMatchers,
        patternMatcherResult = patternMatcherResult,
        clearPatternDelay = clearPatternDelay,
        onCleared = onCleared,
    )
}

@Composable
fun <T : Any> PatternLock(
    patternProvider: PatternProvider<T>,
    modifier: Modifier = Modifier,
    @IntRange(from = 1) cellCount: Int = 3,
    dotSize: Dp = 18.dp,
    selectedDotSize: Dp = 24.dp,
    lineStroke: Dp = 3.dp,
    extraTouch: Dp = 16.dp,
    colors: PatternLockColors = PatternLockDefaults.colors(),
    patternMatchers: List<PatternMatcher<T>> = emptyList(),
    patternMatcherResult: ((result: PatternResult<T>) -> Unit)? = null,
    clearPatternDelay: Long = 800L,
    onCleared: (() -> Unit)? = null,
) {
    val density = LocalDensity.current
    val extraTouchPx = with(density) { extraTouch.toPx() }
    val dotSizePx = with(density) { dotSize.toPx() }
    val selectedDotSizePx = with(density) { selectedDotSize.toPx() }
    val linesStrokePx = with(density) { lineStroke.toPx() }
    val touchArea = dotSizePx + extraTouchPx
    val cells = cellCount - 1

    val clearScope = rememberCoroutineScope()
    var clearJob: Job? = remember { null }

    var selectedDotColor by remember { mutableStateOf(colors.selectedDotColor) }
    var selectedDotContainerColor by remember { mutableStateOf(colors.selectedDotContainer) }
    var patternColor by remember { mutableStateOf(colors.patternColor) }

    var size by remember { mutableStateOf(IntSize.Zero) }

    val dots by remember {
        derivedStateOf { getDotsPositionedEvenly(cells, size, selectedDotSizePx) }
    }
    val inputHandler by remember {
        derivedStateOf { PatternInputHandler(touchArea, dots) }
    }

    val updateColors: (result: Boolean) -> Unit = {
        if (it) {
            selectedDotColor = colors.selectedDotColor
            patternColor = colors.patternColor
            selectedDotContainerColor = colors.selectedDotContainer
        } else {
            selectedDotColor = colors.wrongDotColor
            patternColor = colors.wrongPatternColor
            selectedDotContainerColor = colors.wrongSelectedDotContainer
        }
    }

    val finishClearJob: () -> Unit = {
        updateColors(true)
        clearJob?.cancel()
        clearJob = null
        onCleared?.invoke()
    }

    fun onDragEnd(dots: List<Dot>) {
        if (dots.isEmpty()) return

        val cellList = dots.map { it.cell }
        val pattern = patternProvider.build(cellList)
        var result = PatternResult(
            pattern = pattern,
            cells = cellList,
            invalidator = null,
        )

        if (patternMatchers.isNotEmpty()) {
            val invalidator = patternMatchers.firstOrNull {
                it.match(cellList, pattern).not()
            }
            result = result.copy(invalidator = invalidator)

            updateColors(result.success)
        }

        if (clearPatternDelay >= 0) {
            clearJob = clearScope.launch {
                delay(clearPatternDelay)
                if (isActive) {
                    inputHandler.clear()
                    finishClearJob()
                }
            }
        }

        patternMatcherResult?.invoke(result)
    }

    Canvas(
        modifier = modifier
            .onSizeChanged { size = it }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        if (inputHandler.onDragStart(offset)) {
                            finishClearJob()
                        }
                    },
                    onDrag = { change, _ ->
                        inputHandler.onDrag(change.position)
                    },
                    onDragEnd = {
                        onDragEnd(inputHandler.onDragEnd())
                    },
                    onDragCancel = {
                        onDragEnd(inputHandler.onDragEnd())
                    }
                )
            }
    ) {
        for (dot in dots) {
            val isSelected = dot in inputHandler.connectedDots

            drawCircle(
                color = if (isSelected) selectedDotColor else colors.dotColor,
                radius = dotSizePx / 2f,
                center = dot.offset
            )

            if (dot in inputHandler.connectedDots) {
                drawCircle(
                    color = selectedDotContainerColor,
                    radius = selectedDotSizePx / 2f,
                    center = dot.offset
                )
            }
        }

        if (
            inputHandler.currentLine.start != Offset.Unspecified ||
            inputHandler.currentLine.end != Offset.Unspecified
        ) {
            drawLine(
                color = patternColor,
                start = inputHandler.currentLine.start,
                end = inputHandler.currentLine.end,
                strokeWidth = linesStrokePx,
                cap = StrokeCap.Round
            )
        }

        for (line in inputHandler.connectedLines) {
            drawLine(
                color = patternColor,
                start = line.start,
                end = line.end,
                strokeWidth = linesStrokePx,
                cap = StrokeCap.Round
            )
        }
    }
}


object PatternLockDefaults {

    @Composable
    fun colors(
        dotColor: Color = colorScheme.outline,
        selectedDotColor: Color = colorScheme.primary,
        selectedDotContainer: Color = selectedDotColor.copy(alpha = 0.5f),
        lineColor: Color = selectedDotColor,
        wrongDotColor: Color = colorScheme.error,
        wrongLineColor: Color = wrongDotColor,
        wrongSelectedDotContainer: Color = wrongDotColor.copy(alpha = 0.5f),
    ) = PatternLockColors(
        dotColor = dotColor,
        selectedDotColor = selectedDotColor,
        patternColor = lineColor,
        wrongDotColor = wrongDotColor,
        wrongPatternColor = wrongLineColor,
        selectedDotContainer = selectedDotContainer,
        wrongSelectedDotContainer = wrongSelectedDotContainer,
    )

}

data class PatternLockColors(
    val dotColor: Color,
    val selectedDotColor: Color,
    val selectedDotContainer: Color,
    val patternColor: Color,
    val wrongDotColor: Color,
    val wrongPatternColor: Color,
    val wrongSelectedDotContainer: Color,
)


data class PatternResult<T>(
    val pattern: T,
    val cells: List<Cell>,
    val invalidator: PatternMatcher<T>?,
) {
    val success = invalidator == null
}

