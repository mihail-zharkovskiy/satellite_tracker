package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.remote_sorce.network.TleDto

interface SatellitesRemoteSource {
    suspend fun getAllTle(): List<TleDto>
}
