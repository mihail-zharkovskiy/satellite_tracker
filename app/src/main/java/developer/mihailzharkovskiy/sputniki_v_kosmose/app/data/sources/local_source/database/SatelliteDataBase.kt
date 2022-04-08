package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SatelliteEntity::class],
    version = 1,
    exportSchema = true
)
abstract class SatelliteDataBase : RoomDatabase() {
    abstract fun entriesDao(): SatelliteDao
}


