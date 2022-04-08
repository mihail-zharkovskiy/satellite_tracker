package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.map_view_wrapper

import android.graphics.Color
import android.graphics.Paint
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.MapSatUiData
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.FolderOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline

class MapViewWrapper(
    private val mapView: MapView,
    private val clickOnMarkerMapListener: ClickOnMarkerMapListener,
) {

    interface ClickOnMarkerMapListener {
        fun clickOnMarkerMap(idSatellite: Int)
    }

    var showIcon: Boolean = true
    private val backgroundColor = ContextCompat.getColor(mapView.context, R.color.heavy_clouds)
    private val satAndTrackColor = ContextCompat.getColor(mapView.context, R.color.snow)
    private val satIcon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_satellite)
    private val satStrokeIcon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_sat_stroke)
    private val userPositionIcon = ContextCompat.getDrawable(mapView.context, R.drawable.ic_map_pos)

    private val minLat = MapView.getTileSystem().minLatitude
    private val maxLat = MapView.getTileSystem().maxLatitude
    private val trackSatellitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        strokeWidth = 2f
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeJoin = Paint.Join.ROUND
    }

    init {
        initMap()
        renderMapView()
    }

    fun invalidateUserLocation(userPosition: Coordinates) {
        renderUserPos(userPosition)
        mapView.invalidate()
    }

    fun invalidateSatellites(
        posMap: List<MapSatUiData>,
        satTrack: List<Coordinates>,
        satFootprint: Coordinates,
    ) {
        renderSatelliteStroke(satFootprint)
        renderSatelliteTrack(satTrack)
        renderSatellitePositions(posMap)
        mapView.invalidate()
    }

    private fun initMap() {
        Configuration
            .getInstance()
            .load(mapView.context, PreferenceManager.getDefaultSharedPreferences(mapView.context))
    }

    private fun getMinZoom(screenHeight: Int): Double {
        return MapView.getTileSystem().getLatitudeZoom(maxLat, minLat, screenHeight)
    }

    private fun renderMapView() {
        mapView.apply {
            setMultiTouchControls(true)
            setTileSource(TileSourceFactory.WIKIMEDIA)
            minZoomLevel = getMinZoom(resources.displayMetrics.heightPixels)
            maxZoomLevel = 7.75
            controller.setZoom(minZoomLevel + 0.25)
            zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
            overlayManager.tilesOverlay.loadingBackgroundColor = satAndTrackColor
            overlayManager.tilesOverlay.loadingLineColor = backgroundColor
            overlayManager.tilesOverlay.setColorFilter(getColorFilter())
            setScrollableAreaLimitLatitude(maxLat, minLat, 0)
            // add overlays: 0 - GPS, 1 - SatTrack, 2 - SatGranici, 3 - SatMarkers
            overlays.addAll(Array(4) { FolderOverlay() })
        }
    }

    private fun drawSatelliteMarkerText(satellite: MapSatUiData): Marker {
        return Marker(mapView).apply {
            textLabelBackgroundColor = backgroundColor
            textLabelForegroundColor = satAndTrackColor
            setInfoWindow(null)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            setTextIcon(satellite.name)
            setOnMarkerClickListener { _, _ ->
                clickOnMarkerMapListener.clickOnMarkerMap(satellite.idSatellite)
                return@setOnMarkerClickListener true
            }
            try {
                position = GeoPoint(satellite.latitude, satellite.longitude)
            } catch (e: Exception) {
                Log.d("error_cordinate", "${e.message}")
            }
        }
    }

    private fun drawSatelliteMarkerIcon(satellite: MapSatUiData): Marker {
        return Marker(mapView).apply {
            icon = satIcon
            setInfoWindow(null)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            setOnMarkerClickListener { _, _ ->
                clickOnMarkerMapListener.clickOnMarkerMap(satellite.idSatellite)
                return@setOnMarkerClickListener true
            }
            try {
                position = GeoPoint(satellite.latitude, satellite.longitude)
            } catch (e: Exception) {
                Log.d("error_sat_coordinate", "${e.message}")
            }
        }
    }

    private fun renderUserPos(userPosition: Coordinates) {
        Marker(mapView).apply {
            setInfoWindow(null)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            icon = userPositionIcon
            position = GeoPoint(userPosition.latitude, userPosition.longitude)
            mapView.overlays[0] = this
        }
    }

    private fun renderSatellitePositions(posMap: List<MapSatUiData>) {
        val markersOverlay = FolderOverlay()
        if (showIcon) posMap.forEach { markersOverlay.add(drawSatelliteMarkerIcon(it)) }
        else posMap.forEach { markersOverlay.add(drawSatelliteMarkerText(it)) }
        mapView.overlays[3] = markersOverlay
    }

    private fun renderSatelliteTrack(satTrack: List<Coordinates>) {
        val trackSatellitesOverlay = FolderOverlay()
        val track = satTrack.map { GeoPoint(it.latitude, it.longitude) }
        Polyline().apply {
            outlinePaint.set(trackSatellitePaint)
            setPoints(track)
            trackSatellitesOverlay.add(this)
        }
        mapView.overlays[1] = trackSatellitesOverlay
    }

    private fun renderSatelliteStroke(satFootprint: Coordinates) {
        Marker(mapView).apply {
            setInfoWindow(null)
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = satStrokeIcon
            position = GeoPoint(satFootprint.latitude, satFootprint.longitude)
            mapView.overlays[2] = this
        }
    }
}
