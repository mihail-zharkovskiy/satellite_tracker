package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model

data class MapSatUiData(
    val idSatellite: Int,
    val name: String,
    val range: String,
    val altitude: String,
    val velocity: String,
    val latitudeString: String,
    val longitudeString: String,
    val latitude: Double,
    val longitude: Double,
)
