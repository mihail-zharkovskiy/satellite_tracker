package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository

import androidx.annotation.Keep
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.mapper.mapToDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.mapper.mapToEntity
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.SatelliteEntity
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.SatelliteRepository
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.model.SatelliteDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.Satellite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@Keep
class SatellitesRepositoryImpl @Inject constructor(
    private val localSource: SatellitesLocalSourse,
    private val remoteSource: SatellitesRemoteSource,
) : SatelliteRepository {

    override suspend fun saveDataFromNetwork() {
        val tles = remoteSource.getAllTle().map { tleDto -> tleDto.mapToEntity() }
        val satellites = tles.map { SatelliteEntity(it) }
        localSource.saveDataFromWeb(satellites)
    }

    override suspend fun getAllSatellites(): Flow<List<SatelliteDomainModel>> {
        return localSource.getAllSatellites()
            .map { list -> list.map { entity -> entity.mapToDomainModel() } }
    }

    override suspend fun getSelectedSatellites(): Flow<List<SatelliteDomainModel>> {
        return localSource.getSelectedSatellites()
            .map { list -> list.map { entity -> entity.mapToDomainModel() } }
    }

    override suspend fun getSelectedSatellitesForCalculation(): List<Satellite> {
        return localSource.getSelectedSatellitesForCalculation().map { entity ->
            entity.mapToDomainModel().tle.createSat()
        }
    }

    override suspend fun updateSelection(idSatellites: Int, isSelected: Boolean) {
        localSource.updateSelectedEntitys(idSatellites, isSelected)
    }
}