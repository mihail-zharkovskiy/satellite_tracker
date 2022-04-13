package developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_position

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.convertLatitudeForMap
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.convertLongitudeForMap
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * @param azimuth в радианах, НЕ в градусах
 * @param elevation в радианах, НЕ в градусах
 * @param latitude в радианах, НЕ в градусах
 * @param longitude в радианах, НЕ в градусах
 * **/
data class SatellitePositionDomainModel(
    val idSatellite: Int,
    val name: String,
    var azimuth: Double = 0.0,
    var elevation: Double = 0.0,
    var latitude: Double = 0.0,
    var longitude: Double = 0.0,
    var altitude: Double = 0.0,
    var range: Double = 0.0,
    var rangeRate: Double = 0.0,
    var theta: Double = 0.0,
    var time: Long = 0L,
) {
    fun getOrbitalVelocity(): Double {
        val earthG = 6.674 * 10.0.pow(-11)
        val earthM = 5.98 * 10.0.pow(24)
        val radius = 6.37 * 10.0.pow(6) + altitude * 10.0.pow(3)
        return sqrt(earthG * earthM / radius) / 1000
    }

    fun getCoordinatesForMap(): Coordinates {
        val lat = convertLatitudeForMap(Math.toDegrees(latitude))
        val lon = convertLongitudeForMap(Math.toDegrees(longitude))
        return Coordinates(latitude = lat, longitude = lon)
    }
}
