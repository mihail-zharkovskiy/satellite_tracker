package developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.repository.mapper

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.SatelliteEntity
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.data.sources.local_source.database.TleEntity
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.model.SatelliteDomainModel

fun SatelliteDomainModel.mapToSatelliteEntity(): SatelliteEntity {
    return SatelliteEntity(
        isSelected = this.isSelected,
        tle = TleEntity(
            name = this.tle.name,
            epoch = this.tle.epoch,
            meanmo = this.tle.meanmo,
            eccn = this.tle.eccn,
            incl = this.tle.incl,
            raan = this.tle.raan,
            argper = this.tle.argper,
            meanan = this.tle.meanan,
            idSatellite = this.tle.idSatellite,
            bstar = this.tle.bstar
        )
    )
}