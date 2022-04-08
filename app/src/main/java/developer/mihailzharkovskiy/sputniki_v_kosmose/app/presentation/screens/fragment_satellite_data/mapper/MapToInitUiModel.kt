package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.mapper

import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.azimuthToSideWorld
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model.SatelliteDataInitUiModel

fun SatAboveTheUserDomainModel.mapToInitUiModel(resource: Resource): SatelliteDataInitUiModel {
    return SatelliteDataInitUiModel(
        satName = this.name,
        satellite = this,
        azimuthEnd = String.format(
            resource.getString(R.string.dsf_end_azimt),
            this.endAzimuth,
            this.endAzimuth.azimuthToSideWorld(resource)),
        azimuthStart = String.format(
            resource.getString(R.string.dsf_start_azimt),
            this.startAzimuth,
            this.startAzimuth.azimuthToSideWorld(resource)),
    )
}