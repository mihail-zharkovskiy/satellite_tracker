package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce

import androidx.annotation.Keep
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.SatellitesRemoteSource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network.SatellitesRetrofitService
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network.TleDto
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.di.module.dispatchers.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

@Keep
class SatellitesRemoteSourceImpl @Inject constructor(
    private val retrofitService: SatellitesRetrofitService,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
) : SatellitesRemoteSource {

    //на самом деле можно не указывать диспатчер, тк retrofit сам переключает на IO,
    //но для красоты всеже указал withContext(Dispatchers.IO)

    override suspend fun getAllTle(): List<TleDto> = withContext(ioDispatcher) {
        val data = mutableListOf<TleDto>()
        data.addAll(retrofitService.getStationskSatelites())
        data.addAll(retrofitService.getVisualSatelites())
        data.addAll(retrofitService.getWeatherSatelites())
        data
    }
}