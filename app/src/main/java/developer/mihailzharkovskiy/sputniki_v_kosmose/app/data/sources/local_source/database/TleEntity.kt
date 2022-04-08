package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database

data class TleEntity(
    val name: String,
    val epoch: Double,
    val meanmo: Double,
    val eccn: Double,
    val incl: Double,
    val raan: Double,
    val argper: Double,
    val meanan: Double,
    val idSatellite: Int,
    val bstar: Double,
)