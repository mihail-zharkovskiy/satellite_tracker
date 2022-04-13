package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.extention

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build

/**@return true если интернет работает false если нет**/
fun Context.checkInternetConnection(): Boolean {
    val connectManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    if (Build.VERSION.SDK_INT >= 23) {
        val internetInfo = connectManager.getNetworkCapabilities(connectManager.activeNetwork)
        if (internetInfo != null) {
            when {
                internetInfo.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> return true
                internetInfo.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> return true
                internetInfo.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> return true
            }
        }
    } else {
        val internetInfo = connectManager.activeNetworkInfo
        if (internetInfo != null && internetInfo.isConnected) return true
    }
    return false
}


