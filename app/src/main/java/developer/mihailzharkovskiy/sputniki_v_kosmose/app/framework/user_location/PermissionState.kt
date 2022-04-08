package developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location

sealed class PermissionState {
    object YesPermission : PermissionState()
    object NoPermission : PermissionState()
}