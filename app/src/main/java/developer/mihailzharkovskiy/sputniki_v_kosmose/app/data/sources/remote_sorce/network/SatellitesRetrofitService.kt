package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network

import androidx.annotation.Keep
import retrofit2.http.GET

/**
 *https://celestrak.com/NORAD/elements/gp.php?GROUP=starlink&FORMAT=json  // 2000 спутников
 *https://celestrak.com/NORAD/elements/gp.php?GROUP=visual&FORMAT=json
 *https://celestrak.com/NORAD/elements/gp.php?GROUP=active&FORMAT=json // 5000 спутников
 * **/

@Keep
interface SatellitesRetrofitService {

    @GET("NORAD/elements/gp.php?GROUP=visual&FORMAT=json")
    suspend fun getVisualSatelites(): List<TleDto>

    @GET("NORAD/elements/gp.php?GROUP=weather&FORMAT=json")
    suspend fun getWeatherSatelites(): List<TleDto>

    @GET("NORAD/elements/gp.php?GROUP=stations&FORMAT=json")
    suspend fun getStationskSatelites(): List<TleDto>

//    @GET("NORAD/elements/gp.php")
//    suspend fun getVisualSatellites(
//        @Query("GROUP") typeSatellites:TypeSatellites = TypeSatellites.Visual,
//        @Query("FORMAT") responseType: String = "json"
//    ): List<TleDto>
}
