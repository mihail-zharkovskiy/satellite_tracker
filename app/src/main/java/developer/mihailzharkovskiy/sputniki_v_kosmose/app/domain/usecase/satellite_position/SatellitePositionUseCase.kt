package developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_position

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.di.DefaultDispatcher
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.Satellite
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SatellitePositionUseCase @Inject constructor(
    @DefaultDispatcher private val defaultDispatcher: CoroutineDispatcher,
) {

    suspend fun getPositionSat(
        sat: Satellite,
        userPos: Coordinates,
        time: Long,
    ): SatellitePositionDomainModel {
        return withContext(defaultDispatcher) {
            sat.getPosition(userPos, time)
        }
    }

    suspend fun getTrackSatellite(
        sat: Satellite,
        userPos: Coordinates,
        startTime: Long,
        endTime: Long,
    ): List<SatellitePositionDomainModel> {
        return withContext(defaultDispatcher) {
            val positions = mutableListOf<SatellitePositionDomainModel>()
            var currentTime = startTime
            while (currentTime < endTime) {
                positions.add(sat.getPosition(userPos, currentTime))
                currentTime += 15000
            }
            positions
        }
    }
}
