package developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.model

data class SatelliteDomainModel(
    val tle: TleDomainModel,
    var isSelected: Boolean = false,
)



