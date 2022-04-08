package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates

data class MapUiData(
    val satellites: List<MapSatUiData>,
    val satTrack: List<Coordinates>,
    val satFootprint: Coordinates,
    val satData: MapSatUiData,
)
