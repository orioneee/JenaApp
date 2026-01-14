package sample.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.oriooneee.jet.navigation.presentation.NavigationScreen

@Composable
fun App() {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        NavigationScreen()
    }
}