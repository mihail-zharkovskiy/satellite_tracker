package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.mapper

import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_position.SatellitePositionDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.azimuthToSideWorld
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model.SatelliteDataUiModel

fun SatellitePositionDomainModel.mapToSatDataUiModel(resource: Resource): SatelliteDataUiModel {
    return SatelliteDataUiModel(
        name = this.name,
        range = String.format(resource.getString(R.string.dsf_km), this.range),
        velocity = String.format(resource.getString(R.string.dsf_km_s), this.getOrbitalVelocity()),
        altitude = String.format(resource.getString(R.string.dsf_km), this.altitude),
        elevation = String.format(resource.getString(R.string.dsf_elevation_degree),
            Math.toDegrees(this.elevation)),
        azimuth = String.format(
            resource.getString(R.string.dsf_azimut_curent),
            Math.toDegrees(this.azimuth),
            Math.toDegrees(this.azimuth).azimuthToSideWorld(resource))
    )
}
