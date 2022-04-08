package developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.internet

sealed class InternetState {
    object Off : InternetState()
    object On : InternetState()
}
