package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model

/**
 * Sat - сокращения от satellite
 * @param azimuth НЕ в радианах,а в градусах (не забывай переводить через Math.toDegrees()))
 * @param elevation НЕ в радианах,а в градусах (не забывай переводить через  Math.toDegrees())
 * **/
data class SatDataUiModel(
    val name: String,
    /**НЕ в радианах, в градусах**/
    val azimuth: String,
    /**НЕ в радианах, в градусах**/
    val elevation: String,
    val altitude: String,
    val range: String,
    val velocity: String,

    )
