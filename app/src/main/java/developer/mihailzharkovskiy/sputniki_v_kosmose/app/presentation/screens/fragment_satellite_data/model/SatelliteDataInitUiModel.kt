package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserDomainModel

/**нужено для обработки данных переданных во время пперехода меж экранами**/
data class SatelliteDataInitUiModel(
    val satName: String,
    val azimuthEnd: String,
    val azimuthStart: String,
    val satellite: SatAboveTheUserDomainModel,
)
