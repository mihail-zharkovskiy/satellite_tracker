package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model

/**
 * @param azimuth НЕ в радианах,а в градусах (не забывай переводить через Math.toDegrees()))
 * @param elevation НЕ в радианах,а в градусах (не забывай переводить через  Math.toDegrees())
 * **/
data class SatelliteDataUiModel(
    val name: String,
    /**НЕ в радианах, в градусах**/
    val azimuth: String,
    /**НЕ в радианах, в градусах**/
    val elevation: String,
    val altitude: String,
    val range: String,
    val velocity: String,

    )
