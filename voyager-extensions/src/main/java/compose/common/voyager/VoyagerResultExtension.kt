package compose.common.voyager

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import cafe.adriel.voyager.core.lifecycle.ScreenDisposable
import cafe.adriel.voyager.core.lifecycle.ScreenLifecycleStore
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.navigator.currentOrThrow
import cafe.adriel.voyager.navigator.lifecycle.NavigatorDisposable
import cafe.adriel.voyager.navigator.lifecycle.NavigatorLifecycleStore

@Composable
fun <T> rememberLauncherForScreenResult(
    onResult: (T?) -> Unit
): ScreenResultLauncher {
    val currentOnResult by rememberUpdatedState(onResult)
    val navigator = LocalNavigator.currentOrThrow
    val resultExtension = rememberNavigationResultExtension()

    val launcher = remember {
        ScreenResultLauncherImpl(
            navigator = navigator,
            onDispose = { screenKey ->
                val result = resultExtension.getResult<T>(screenKey)
                currentOnResult(result)
            }
        )
    }

    return launcher
}


@Composable
public fun rememberNavigationResultExtension(): VoyagerResultExtension {
    val navigator = LocalNavigator.currentOrThrow

    return remember {
        NavigatorLifecycleStore.get(navigator) {
            VoyagerResultExtension(navigator)
        }
    }
}

class VoyagerResultExtension(
    private val navigator: Navigator
) : NavigatorDisposable {
    private val results = mutableStateMapOf<String, Any?>()

    override fun onDispose(navigator: Navigator) {
        results.clear()
    }

    public fun setResult(key: String, result: Any?) {
        results[key] = result
    }

    public fun popWithResult(result: Any?) {
        val currentScreen = navigator.lastItem
        results[currentScreen.key] = result
        navigator.pop()
    }

    public fun <T> getResult(key: String): T? {
        val result = results[key] as? T
        results.remove(key)
        return result
    }
}


interface ScreenResultLauncher {
    fun launch(screen: Screen)
}

internal class ScreenResultLauncherImpl(
    private val navigator: Navigator,
    private val onDispose: (key: String) -> Unit,
) : ScreenResultLauncher {

    private val disposable = object : ScreenDisposable {
        override fun onDispose(screen: Screen) {
            onDispose(screen.key)
        }
    }

    override fun launch(screen: Screen) {
        navigator navigate screen

        ScreenLifecycleStore.get(screen) { disposable }
    }
}