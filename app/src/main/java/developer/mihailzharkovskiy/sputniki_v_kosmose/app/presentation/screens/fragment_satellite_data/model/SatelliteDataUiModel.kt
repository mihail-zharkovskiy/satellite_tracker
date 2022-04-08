package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model

/**
 * @param azimuth НЕ в радианах, в градусах
 * @param elevation НЕ в радианах, в градусах
 * **/
data class SatelliteDataUiModel(
    val name: String,
    val azimuth: String,
    val elevation: String,
    val altitude: String,
    val range: String,
    val velocity: String,

//    val name: String = "",
//    val azimuth: String = "",
//    val elevation: String = "",
//    val altitude: String = "",
//    val range: String = "",
//    val velocity:String = "",
)
