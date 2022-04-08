package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.model

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserDomainModel

data class SatAboveTheUserUiModel(
    val satId: Int,
    val name: String,
    var progress: Int = 0,
    val isDeepSpace: Boolean,
    val startTime: String,
    val endTime: String,
    val startAzimuth: String,
    val endAzimuth: String,
    val centerAzimuth: String,
    val maxElevation: String,
    val satAboveTheUserDomainModel: SatAboveTheUserDomainModel,
)

