package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce

import androidx.annotation.Keep
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.SatellitesRemoteSource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network.SatellitesRetrofitService
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network.TleDto
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.di.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Keep
class SatellitesRemoteSourceImpl @Inject constructor(
    private val retrofitService: SatellitesRetrofitService,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
) : SatellitesRemoteSource {

    //withContext(Dispatchers.IO) сделал только для красоты. Retrofit может и сам переключить потоки

    override suspend fun getAllTle(): List<TleDto> = withContext(ioDispatcher) {
        val data = mutableListOf<TleDto>()
        data.addAll(retrofitService.getStationsSatellites())
        data.addAll(retrofitService.getVisualSatellites())
        data.addAll(retrofitService.getWeatherSatellites())
        data
    }
}