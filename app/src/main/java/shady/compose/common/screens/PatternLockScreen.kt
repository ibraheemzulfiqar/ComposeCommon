package shady.compose.common.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.core.screen.uniqueScreenKey
import compose.common.pattern.PatternLengthMatcher
import compose.common.pattern.PatternLock
import compose.common.pattern.minPatternOf

class PatternLockScreen : Screen {

    override val key: ScreenKey = uniqueScreenKey

    @Composable
    override fun Content() {
        Scaffold { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                PatternLock(
                    modifier = Modifier
                        .padding(16.dp)
                        .height(270.dp)
                        .aspectRatio(1f),
                    patternMatchers = listOf(minPatternOf(4)),
                    extraTouch = 16.dp,
                )
            }
        }
    }
}