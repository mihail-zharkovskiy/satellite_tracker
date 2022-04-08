package developer.mihailzharkovskiy.sputniki_v_kosmose.app

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.internet.InternetState

fun Context.checkInternetConnection(): InternetState {
    val connectManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= 23) {
        val internetInfo = connectManager.getNetworkCapabilities(connectManager.activeNetwork)
        if (internetInfo != null) {
            when {
                internetInfo.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return InternetState.On
                internetInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return InternetState.On
                internetInfo.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return InternetState.On
            }
        }
    } else {
        val internetInfo = connectManager.activeNetworkInfo
        if (internetInfo != null && internetInfo.isConnected) return InternetState.On
    }
    return InternetState.Off
}


