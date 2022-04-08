package developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.internet

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.InternetStateChanges

class ReceiverInternetChanges(private val callbackInternetChanges: InternetStateChanges) :
    BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ConnectivityManager.CONNECTIVITY_ACTION) {
            callbackInternetChanges.emit(checkInternetConnection(context))
        }
    }

    fun registerReceiver(context: Context) {
        val intentFilter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        context.registerReceiver(this, intentFilter)
    }

    fun unRegisterReceiver(context: Context) {
        context.unregisterReceiver(this)
    }

    private fun checkInternetConnection(context: Context): InternetState {
        val connectManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
}
