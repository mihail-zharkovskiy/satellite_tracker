package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.mapper

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.model.SatelliteDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.model.EntriesUiModel

fun SatelliteDomainModel.mapToUiModel(): EntriesUiModel {
    return EntriesUiModel(
        idSatellite = this.tle.idSatellite,
        nameSatellite = this.tle.name,
        isSelected = this.isSelected
    )
}

