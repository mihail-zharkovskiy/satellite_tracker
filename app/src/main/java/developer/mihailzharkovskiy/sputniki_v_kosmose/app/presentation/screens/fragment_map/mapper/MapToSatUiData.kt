package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.mapper

import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_position.SatellitePositionDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.MapSatUiData
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.convertLatitudeForMap
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.convertLongitudeForMap

fun SatellitePositionDomainModel.toMapSatUiData(resource: Resource): MapSatUiData {
    return MapSatUiData(
        idSatellite = this.idSatellite,
        name = this.name,
        range = String.format(resource.getString(R.string.fm_range), this.range),
        altitude = String.format(resource.getString(R.string.fm_altitude), this.altitude),
        velocity = String.format(resource.getString(R.string.fm_speed), this.getOrbitalVelocity()),
        latitudeString = String.format(resource.getString(R.string.fm_latitude),
            Math.toDegrees(this.latitude)),
        longitudeString = String.format(resource.getString(R.string.fm_longitude),
            Math.toDegrees(this.longitude)),

        /**НУЖНА ЛИ КОНВЕРТАЦИЯ?**/
        latitude = convertLatitudeForMap(Math.toDegrees(this.latitude)),
        longitude = convertLongitudeForMap(Math.toDegrees(this.longitude))
    )
}

