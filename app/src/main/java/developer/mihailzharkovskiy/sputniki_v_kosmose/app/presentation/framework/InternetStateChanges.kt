package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.internet.InternetState

interface InternetStateChanges {
    fun emit(internetState: InternetState)
}
