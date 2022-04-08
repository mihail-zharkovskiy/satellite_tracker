package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.state

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.MapUiData

data class MapUiState(
    val mapDataState: DataState<MapUiData>,
//    val locationState: PermissionState,
)


//sealed class MapUiState {
//    class NormalWork(
//        val dataState: DataState<MapUiData>,
//        val locationState: UserLocationState
//    ) : MapUiState()
////    class NoLocationPermission (val locationState: UserLocationState) : MapUiState()
//}
