package developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.model.SatelliteDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.Satellite
import kotlinx.coroutines.flow.Flow

interface SatelliteRepository {

    suspend fun saveDataFromNetwork()
    suspend fun getAllSatellites(): Flow<List<SatelliteDomainModel>>
    suspend fun getSelectedSatellites(): Flow<List<SatelliteDomainModel>>
    suspend fun getSelectedSatellitesForCalculation(): List<Satellite>
    suspend fun updateSelection(idSatellites: Int, isSelected: Boolean)
}