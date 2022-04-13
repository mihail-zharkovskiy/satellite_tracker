package developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.content.ContextCompat
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.UserLocationSource
import javax.inject.Inject

/* можно бы было бы не возращать из функци результат определения локации,
 * а организавть flow, но такой вариант получался более объемный и геморойным.
 *
 * NB!!! если приспичит,что бы помимо как через сеть координаты определялись еще и через gps
 * не забудь: если есть разрешение Manifest.permission.ACCESS_COARSE_LOCATION
 * значит можно делать запрос чере network provider но нельзя через gps provider
 * если есть разрешение Manifest.permission.ACCESS_FINE_LOCATION то можно делать через оба провайдера
 * И главное!!! начиная с 10 андроида больно много возни для работы с gps provider => оставляй до последнего
 * только один network provider
 */

class UserLocationSourceImpl @Inject constructor(
    private val context: Context,
) : LocationListener, UserLocationSource {

    private val permCoarseLoc = Manifest.permission.ACCESS_COARSE_LOCATION
    private val userLocationDefault = Coordinates(0.0, 0.0)

    private val locManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun getUserLocation(): Coordinates {
        return if (checkPermission() == PermissionState.YesPermission) {
            val data = locManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            if (data != null) Coordinates(data.latitude, data.longitude)
            else userLocationDefault
        } else userLocationDefault
    }


    override fun updateUserLocation(): UpdateUserLocationState {
        val permission = checkPermission()
        val providerNetwork = LocationManager.NETWORK_PROVIDER
        return when {
            locManager.isProviderEnabled(providerNetwork) && permission == PermissionState.YesPermission -> {
                locManager.requestLocationUpdates(providerNetwork, 1L, 1f, this)
                val result = locManager.getLastKnownLocation(providerNetwork)
                val latitude = result?.latitude ?: 0.0
                val longitude = result?.longitude ?: 0.0
                UpdateUserLocationState.Success(Coordinates(latitude, longitude))
            }
            permission == PermissionState.NoPermission -> {
                UpdateUserLocationState.Error(
                    context.getString(R.string.ul_snack_bar_no_access_to_location),
                    userLocationDefault)
            }
            else -> {
                /**значит в настройках телефона отключил предоставление данных о местоположении для всех приложений**/
                UpdateUserLocationState.Error(
                    context.getString(R.string.ul_snack_bar_no_access_to_location_for_all_apps),
                    userLocationDefault)
            }
        }
    }

    override fun checkPermission(): PermissionState {
        val resultCoarseLoc = ContextCompat.checkSelfPermission(context, permCoarseLoc)
        return when (resultCoarseLoc == PackageManager.PERMISSION_GRANTED) {
            true -> PermissionState.YesPermission
            false -> PermissionState.NoPermission
        }
    }

    override fun onLocationChanged(location: Location) {
        locManager.removeUpdates(this)
        /**
         * логика: после вызова requestLocationUpdates, в [onLocationChanged] приходит [location]
         * этот результат кэшируется системой, после чего происходит отписка от прослушивания координат
         * locManager.removeUpdates(this). После всегда можно достать эти координаты черз
         * locManager.getLastKnownLocation.
         * вариант чтобы не возиться с flow или shared pref.
         * **/
    }
}

