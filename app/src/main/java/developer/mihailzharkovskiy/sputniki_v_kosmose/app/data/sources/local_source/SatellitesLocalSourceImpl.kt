package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.SatellitesLocalSourse
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.SatelliteDao
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.SatelliteEntity
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.di.module.dispatchers.IoDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SatellitesLocalSourceImpl @Inject constructor(
    private val entriesDao: SatelliteDao,
    @IoDispatcher
    private val ioDispatcher: CoroutineDispatcher,
) : SatellitesLocalSourse {

    //на самом деле можно не указывать диспатчер, тк Room сам переключает если нужно на IO,
    //но для красоты всеже указал withContext(Dispatchers.IO)

    override suspend fun getSelectedSatellites() = withContext(ioDispatcher) {
        entriesDao.getSelectedSatellites()
    }

    override suspend fun getAllSatellites() = withContext(ioDispatcher) {
        entriesDao.getAllSatellites()
    }

    override suspend fun getSelectedSatellitesForCalculation() = withContext(ioDispatcher) {
        entriesDao.getSelectedSatellitesForCalculation()
    }

    override suspend fun updateSelectedEntitys(idSatellites: Int, isSelected: Boolean) =
        withContext(ioDispatcher) {
            entriesDao.updateSatellitesSelection(idSatellites, isSelected)
        }

    override suspend fun saveDataFromWeb(entities: List<SatelliteEntity>) =
        withContext(ioDispatcher) {
            entriesDao.saveDataFromWeb(entities)
        }
}