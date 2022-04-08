package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.state

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model.SatelliteDataUiModel

sealed class SatelliteDataUiState {
    class SatVisible(val progress: Float, val data: DataState<SatelliteDataUiModel>) :
        SatelliteDataUiState()

    class SatInvisible(val data: DataState<SatelliteDataUiModel>) : SatelliteDataUiState()
    object NoData : SatelliteDataUiState()
}
