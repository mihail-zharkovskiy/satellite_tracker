package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.state

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model.SatDataUiModel

/**Sat - сокращения от satellite**/
sealed class SatDataUiState {
    class SatVisible(val progress: Float, val data: DataState<SatDataUiModel>) : SatDataUiState()
    class SatInvisible(val data: DataState<SatDataUiModel>) : SatDataUiState()
    object NoData : SatDataUiState()
}
