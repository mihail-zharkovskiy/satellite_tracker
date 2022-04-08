package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.SatelliteEntity
import kotlinx.coroutines.flow.Flow

interface SatellitesLocalSourse {

    suspend fun getAllSatellites(): Flow<List<SatelliteEntity>>

    suspend fun getSelectedSatellites(): Flow<List<SatelliteEntity>>

    suspend fun getSelectedSatellitesForCalculation(): List<SatelliteEntity>

    suspend fun updateSelectedEntitys(idSatellites: Int, isSelected: Boolean = true)

    suspend fun saveDataFromWeb(entities: List<SatelliteEntity>)

}