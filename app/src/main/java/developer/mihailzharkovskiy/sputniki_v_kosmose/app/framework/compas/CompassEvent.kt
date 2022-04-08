package developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas

sealed class CompassEvent {
    class Data(val azimuth: Int, val azimuthString: String) : CompassEvent()
    class Error(val message: String) : CompassEvent()
    object NoData : CompassEvent()
}