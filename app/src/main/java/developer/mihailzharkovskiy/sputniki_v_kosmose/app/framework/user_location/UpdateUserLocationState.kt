package developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates

sealed class UpdateUserLocationState {
    object Loading : UpdateUserLocationState()
    class Success(val coordinate: Coordinates) : UpdateUserLocationState()
    class Error(val message: String) : UpdateUserLocationState()
}

