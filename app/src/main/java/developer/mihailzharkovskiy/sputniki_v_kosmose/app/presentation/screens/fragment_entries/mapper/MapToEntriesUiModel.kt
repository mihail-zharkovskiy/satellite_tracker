package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.mapper

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.model.SatelliteDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.model.EntriesUiModel

fun SatelliteDomainModel.mapToUiModel(): EntriesUiModel {
    return EntriesUiModel(
        id = this.tle.idSatellite,
        name = this.tle.name,
        isSelected = this.isSelected
    )
}

