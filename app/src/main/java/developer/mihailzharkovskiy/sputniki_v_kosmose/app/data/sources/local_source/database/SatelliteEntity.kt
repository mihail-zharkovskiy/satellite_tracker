package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database

import androidx.room.Embedded
import androidx.room.Entity

@Entity(tableName = "entries", primaryKeys = ["idSatellite"])
data class SatelliteEntity(
    @Embedded
    val tle: TleEntity,
    var isSelected: Boolean = false,
)


