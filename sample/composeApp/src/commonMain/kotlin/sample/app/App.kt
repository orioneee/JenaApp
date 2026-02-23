package sample.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.oriooneee.jena.presentation.navigation.NavigationApp

@Composable
fun App(
    isDarkTheme: Boolean = false
) {
    Surface {
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            NavigationApp(isDarkTheme)
        }
    }
}