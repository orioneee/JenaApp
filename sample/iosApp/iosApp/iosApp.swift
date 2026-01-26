import SwiftUI
import ComposeApp
import MapboxMaps

@main
struct ComposeApp: App {
    init() {
        // Initialize MapBox token before any map views are created
        MapboxOptions.accessToken = MainKt.GetMapBoxToken()
    }

    var body: some Scene {
        WindowGroup {
            ContentView().ignoresSafeArea(.all)
        }
    }
}

struct ContentView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        return MainKt.MainViewController(nativeFactory: IosNativeViewFactory(), isDarkMode: false)
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
        // Updates will be handled by Compose
    }
}
