//
//  IosNativeViewFactory.swift
//  iosApp
//
//  Created by Admin on 26.01.2026.
//
import UIKit
import MapboxMaps
import ComposeApp
import Combine
import ObjectiveC

class IosNativeViewFactory: JetNavigationNativeFactory {

    func getMapBoxMap(step: JetNavigationNavigationStep.OutDoorMaps?, isDarkTheme: Bool) -> UIView {
        // Create map with standard style and FADED theme
        let styleJSON = createFadedStyleJSON(isDarkTheme: isDarkTheme)

        let mapInitOptions = MapInitOptions(
            styleURI: nil,
            styleJSON: styleJSON
        )

        let mapView = MapView(frame: .zero, mapInitOptions: mapInitOptions)

        // Hide all ornaments
        mapView.ornaments.compassView.isHidden = true
        mapView.ornaments.scaleBarView.isHidden = true
        mapView.ornaments.logoView.isHidden = true
        mapView.ornaments.attributionButton.isHidden = true

        // Also remove from superview to ensure they don't show
        mapView.ornaments.compassView.removeFromSuperview()
        mapView.ornaments.scaleBarView.removeFromSuperview()
        mapView.ornaments.logoView.removeFromSuperview()
        mapView.ornaments.attributionButton.removeFromSuperview()

        // Disable rotation and pitch
        mapView.gestures.options.rotateEnabled = false
        mapView.gestures.options.pitchEnabled = false

        // If we have route points, draw the route
        if let step = step, let path = step.path as? [JetNavigationCoordinates], path.count >= 2 {
            let coordinates = path.map { CLLocationCoordinate2D(latitude: $0.latitude, longitude: $0.longitude) }

            // Add route annotations and set camera after style loads
            mapView.mapboxMap.onStyleLoaded.observeNext { [weak mapView] (event: StyleLoaded) in
                guard let mapView = mapView else { return }

                // Fit camera to route
                let camera = mapView.mapboxMap.camera(
                    for: coordinates,
                    padding: UIEdgeInsets(top: 100, left: 100, bottom: 100, right: 100),
                    bearing: 18.5,
                    pitch: 0
                )
                mapView.camera.fly(to: camera, duration: 0)

                // Add route annotations
                self.addRouteAnnotations(to: mapView, coordinates: coordinates)
            }.store(in: &mapView.cancellables)
        }

        return mapView
    }

    private func createFadedStyleJSON(isDarkTheme: Bool) -> String {
        // Use Standard style with faded theme and appropriate light preset
        let lightPreset = isDarkTheme ? "night" : "day"
        return """
        {
            "version": 8,
            "imports": [
                {
                    "id": "basemap",
                    "url": "mapbox://styles/mapbox/standard",
                    "config": {
                        "theme": "faded",
                        "lightPreset": "\(lightPreset)"
                    }
                }
            ],
            "sources": {},
            "layers": []
        }
        """
    }

    private func addRouteAnnotations(to mapView: MapView, coordinates: [CLLocationCoordinate2D]) {
        let googleBlue = UIColor(red: 66/255, green: 133/255, blue: 244/255, alpha: 1.0)
        let routeBorderColor = UIColor(red: 21/255, green: 88/255, blue: 176/255, alpha: 1.0)

        // Create polyline annotation manager
        let polylineManager = mapView.annotations.makePolylineAnnotationManager()

        // Border line
        var borderAnnotation = PolylineAnnotation(lineCoordinates: coordinates)
        borderAnnotation.lineColor = StyleColor(routeBorderColor)
        borderAnnotation.lineWidth = 10.0
        borderAnnotation.lineJoin = .round
        borderAnnotation.lineEmissiveStrength = 1.0

        // Main route line
        var routeAnnotation = PolylineAnnotation(lineCoordinates: coordinates)
        routeAnnotation.lineColor = StyleColor(googleBlue)
        routeAnnotation.lineWidth = 8.0
        routeAnnotation.lineJoin = .round
        routeAnnotation.lineEmissiveStrength=1.0

        polylineManager.annotations = [borderAnnotation, routeAnnotation]

        // Create circle annotation for start point
        let circleManager = mapView.annotations.makeCircleAnnotationManager()
        circleManager.circleEmissiveStrength = 1.0
        var startCircle = CircleAnnotation(centerCoordinate: coordinates.first!)
        startCircle.circleRadius = 4.0
        startCircle.circleColor = StyleColor(googleBlue)
        startCircle.circleStrokeWidth = 2.0
        startCircle.circleStrokeColor = StyleColor(.white)
        circleManager.annotations = [startCircle]

        // Create point annotation for end marker
        let pointManager = mapView.annotations.makePointAnnotationManager()
        var endMarker = PointAnnotation(coordinate: coordinates.last!)

        // Load marker from assets and scale to appropriate size (preserving aspect ratio)
        if let markerImage = UIImage(named: "ic_map_marker"),
           let scaledImage = scaleImage(markerImage, toHeight: 44) {
            endMarker.image = .init(image: scaledImage, name: "end-marker")
        } else {
            // Fallback: simple circle marker
            endMarker.image = .init(image: createSimpleMarker(), name: "end-marker")
        }
        endMarker.iconAnchor = .bottom

        pointManager.annotations = [endMarker]
    }

    private func scaleImage(_ image: UIImage, toHeight targetHeight: CGFloat) -> UIImage? {
        let scale = targetHeight / image.size.height
        let targetWidth = image.size.width * scale
        let targetSize = CGSize(width: targetWidth, height: targetHeight)

        let renderer = UIGraphicsImageRenderer(size: targetSize)
        return renderer.image { _ in
            image.draw(in: CGRect(origin: .zero, size: targetSize))
        }
    }

    private func createSimpleMarker() -> UIImage {
        let size = CGSize(width: 32, height: 44)
        let renderer = UIGraphicsImageRenderer(size: size)

        return renderer.image { context in
            let ctx = context.cgContext
            let googleBlue = UIColor(red: 66/255, green: 133/255, blue: 244/255, alpha: 1.0)

            // Draw pin shape
            let pinPath = UIBezierPath()
            pinPath.move(to: CGPoint(x: 16, y: 44))
            pinPath.addCurve(to: CGPoint(x: 0, y: 16),
                            controlPoint1: CGPoint(x: 16, y: 44),
                            controlPoint2: CGPoint(x: 0, y: 28))
            pinPath.addArc(withCenter: CGPoint(x: 16, y: 16), radius: 16, startAngle: .pi, endAngle: 0, clockwise: true)
            pinPath.addCurve(to: CGPoint(x: 16, y: 44),
                            controlPoint1: CGPoint(x: 32, y: 28),
                            controlPoint2: CGPoint(x: 16, y: 44))
            pinPath.close()

            // White border
            ctx.setFillColor(UIColor.white.cgColor)
            ctx.addPath(pinPath.cgPath)
            ctx.fillPath()

            // Blue inner
            let innerPath = UIBezierPath()
            innerPath.move(to: CGPoint(x: 16, y: 40))
            innerPath.addCurve(to: CGPoint(x: 4, y: 16),
                              controlPoint1: CGPoint(x: 16, y: 40),
                              controlPoint2: CGPoint(x: 4, y: 26))
            innerPath.addArc(withCenter: CGPoint(x: 16, y: 16), radius: 12, startAngle: .pi, endAngle: 0, clockwise: true)
            innerPath.addCurve(to: CGPoint(x: 16, y: 40),
                              controlPoint1: CGPoint(x: 28, y: 26),
                              controlPoint2: CGPoint(x: 16, y: 40))
            innerPath.close()

            ctx.setFillColor(googleBlue.cgColor)
            ctx.addPath(innerPath.cgPath)
            ctx.fillPath()

            // White center dot
            ctx.setFillColor(UIColor.white.cgColor)
            ctx.fillEllipse(in: CGRect(x: 11, y: 11, width: 10, height: 10))
        }
    }
}

// Extension to store cancellables
extension MapView {
    private static var cancellablesKey: UInt8 = 0

    var cancellables: Set<AnyCancellable> {
        get {
            return objc_getAssociatedObject(self, &MapView.cancellablesKey) as? Set<AnyCancellable> ?? []
        }
        set {
            objc_setAssociatedObject(self, &MapView.cancellablesKey, newValue, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)
        }
    }
}
