package compose.common.voyager

import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.stack.Stack

public infix fun Stack<Screen>.navigate(item: Screen) {
    val lastScreen = lastItemOrNull

    if (lastScreen == null || lastScreen::class != item::class) {
        push(item)
    }
}

public infix fun Stack<Screen>.popThis(screen: Screen) {
    val lastScreen = lastItemOrNull

    if (lastScreen?.key == screen.key) {
        pop()
    }
}