package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.CompassEvent

interface Compass {
    fun startListening(listener: AzimuthListener)
    fun stopListening(listener: AzimuthListener)

    interface AzimuthListener {
        fun onOrientationChanged(compassEvent: CompassEvent)
    }
}

