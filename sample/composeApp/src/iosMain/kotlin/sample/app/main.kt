import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.window.ComposeUIViewController
import com.oriooneee.jena.presentation.LocalNativeFactory
import com.oriooneee.jena.presentation.NativeFactory
import platform.UIKit.UIViewController
import sample.app.App

fun GetMapBoxToken(): String = NativeFactory.getMapBoxToken()

fun MainViewController(
    nativeFactory: NativeFactory,
    isDarkMode: Boolean
): UIViewController = ComposeUIViewController {
    MaterialTheme(
        if(isDarkMode){
            darkColorScheme()
        } else{
            lightColorScheme()
        }
    ) {
        CompositionLocalProvider(
            LocalNativeFactory provides nativeFactory
        ) {
            App(isDarkMode)
        }
    }
}