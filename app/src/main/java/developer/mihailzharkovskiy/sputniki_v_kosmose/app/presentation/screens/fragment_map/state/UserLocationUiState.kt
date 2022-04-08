package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.state

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.PermissionState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.UpdateUserLocationState

data class UserLocationUiState(
    val coordinates: UpdateUserLocationState,
    val permission: PermissionState,
)