package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.PermissionState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.UpdateUserLocationState

interface UserLocationSource {

    fun getUserLocation(): Coordinates

    fun checkPermission(): PermissionState

    fun updateUserLocation(): UpdateUserLocationState
}