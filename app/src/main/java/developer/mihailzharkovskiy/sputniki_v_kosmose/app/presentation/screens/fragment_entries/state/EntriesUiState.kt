package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.state

import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.model.EntriesUiModel

sealed class EntriesUiState {
    class SectionAllSat(val state: DataState<List<EntriesUiModel>>) : EntriesUiState()
    class SectionFavoriteSat(val state: DataState<List<EntriesUiModel>>) : EntriesUiState()
    object Loading : EntriesUiState()
}






