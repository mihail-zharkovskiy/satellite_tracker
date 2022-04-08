package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.mapper

import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.azimuthToSideWorld
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.model.SatAboveTheUserUiModel
import java.text.SimpleDateFormat
import java.util.*

fun SatAboveTheUserDomainModel.mapToSatAboveTheUserUiModel(resource: Resource): SatAboveTheUserUiModel {
    return SatAboveTheUserUiModel(
        satId = this.satId,
        name = this.name,
        progress = this.progress,
        isDeepSpace = this.isDeepSpace,
        startTime = SimpleDateFormat(
            resource.getString(R.string.fps_startTime),
            Locale.getDefault()).format(this.startTime),
        endTime = SimpleDateFormat(
            resource.getString(R.string.fps_endTime),
            Locale.getDefault()).format(this.endTime),
        endAzimuth = String.format(
            resource.getString(R.string.fps_azimuth_communis),
            this.endAzimuth,
            this.endAzimuth.azimuthToSideWorld(resource)),
        startAzimuth = String.format(
            resource.getString(R.string.fps_azimuth_communis),
            this.startAzimuth,
            this.startAzimuth.azimuthToSideWorld(resource)),
        centerAzimuth = String.format(
            resource.getString(R.string.fps_azimuth_communis),
            this.centerAzimuth,
            this.centerAzimuth.azimuthToSideWorld(resource)),
        maxElevation = String.format(
            resource.getString(R.string.fps_maxElevation),
            this.maxElevation),
        satAboveTheUserDomainModel = this
    )
}