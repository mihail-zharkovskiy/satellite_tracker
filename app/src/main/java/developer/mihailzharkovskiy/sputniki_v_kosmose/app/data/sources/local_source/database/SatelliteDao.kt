package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SatelliteDao {

    /**метода для отображения данных но недля расчетов**/
    @Query("SELECT * FROM entries ORDER BY name ASC")
    fun getAllSatellites(): Flow<List<SatelliteEntity>>

    /**метода для отображения данных но недля расчетов**/
    @Query("SELECT * FROM entries WHERE isSelected= :selected  ORDER BY name ASC")
    fun getSelectedSatellites(selected: Boolean = true): Flow<List<SatelliteEntity>>

    /**метода для расчета данных**/
    @Query("SELECT * FROM entries WHERE isSelected= :selected") //можно захоркодить через WHERE isSelected= 1... 1-в room это true 0 это false
    suspend fun getSelectedSatellitesForCalculation(selected: Boolean = true): List<SatelliteEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDataFromWeb(entitys: List<SatelliteEntity>)

    @Query("UPDATE entries SET isSelected = :isSelected WHERE idSatellite = :idSatellite")
    suspend fun updateSatellitesSelection(idSatellite: Int, isSelected: Boolean)
}
