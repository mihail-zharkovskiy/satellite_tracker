package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.staet

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.PermissionState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.model.SatAboveTheUserUiModel

data class SatAboveTheUserUiState(
    val dataState: DataState<List<SatAboveTheUserUiModel>>,
    val permissionState: PermissionState,
)

