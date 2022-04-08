package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user

sealed class SatAboveTheUserEvent {
    object RefreshData : SatAboveTheUserEvent()
    object OnResume : SatAboveTheUserEvent()
    object OnPause : SatAboveTheUserEvent()
}