package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries

import android.text.Editable
import android.text.TextWatcher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.lifecycle.HiltViewModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.SatelliteRepository
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.adapter.EntriesAdapter
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.mapper.mapToUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.model.EntriesUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.state.EntriesUiState
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntriesViewModel @Inject constructor(
    private val repository: SatelliteRepository,
) : ViewModel(),
    EntriesAdapter.EntriesClickListener,
    TextWatcher,
    TabLayout.OnTabSelectedListener {

    private var shouldSelectAll = true
    private var jobObserveSelectedSat: Job? = null
    private var satellites = listOf<EntriesUiModel>()

    private val _uiState = MutableStateFlow<EntriesUiState>(EntriesUiState.Loading)
    val uiState: StateFlow<EntriesUiState> = _uiState.asStateFlow()

    init {
        _uiState.value = EntriesUiState.SectionAllSat(DataState.loading())
        viewModelScope.launch { getAllSat() }
    }

    fun saveDataFromWeb() {
        viewModelScope.launch {
            _uiState.value = EntriesUiState.SectionAllSat(DataState.loading())
            repository.saveDataFromNetwork()
            getAllSat()
        }
    }

    private suspend fun getAllSat() {
        jobObserveSelectedSat?.cancelAndJoin()
        jobObserveSelectedSat = viewModelScope.launch {
            repository.getAllSatellites().collect { list ->
                satellites = list.map { it.mapToUiModel() }
                if (satellites.isNotEmpty()) {
                    _uiState.value = EntriesUiState.SectionAllSat(DataState.success(satellites))
                } else _uiState.value = EntriesUiState.SectionAllSat(DataState.empty())
            }
        }
    }

    private suspend fun getFavoriteSat() {
        jobObserveSelectedSat?.cancelAndJoin()
        jobObserveSelectedSat = viewModelScope.launch {
            repository.getSelectedSatellites().collect { list ->
                val data = list.map { it.mapToUiModel() }
                _uiState.value = EntriesUiState.SectionFavoriteSat(DataState.success(data))
            }
        }
    }

    fun selectAllSatellites() {
        viewModelScope.launch {
            satellites.forEach { repository.updateSelection(it.idSatellite, shouldSelectAll) }
            shouldSelectAll = shouldSelectAll.not()
        }
    }

    override fun clickOnItemAdapter(idSatellites: Int, isSelected: Boolean) {
        viewModelScope.launch { repository.updateSelection(idSatellites, isSelected) }
    }

    /**МЕТОДЫ СЛУШАТЕЛЯ ИЗМЕНЕНИЙ В EDIT TEXT**/
    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        viewModelScope.launch {
            jobObserveSelectedSat?.cancelAndJoin()
            if (text.isEmpty()) {
                getAllSat()
            } else {
                val searchResult = satellites.filter { satellite ->
                    satellite.nameSatellite.startsWith(text, true)
                            || satellite.nameSatellite.contains(text, true)
                }
                _uiState.value = EntriesUiState.SectionAllSat(DataState.success(searchResult))
            }
        }
    }
    override fun afterTextChanged(p0: Editable?) {}
    override fun beforeTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    /**МЕТОДЫ TabLayout.OnTabSelectedListener**/
    override fun onTabSelected(tab: TabLayout.Tab?) {
        when (tab?.position) {
            0 -> viewModelScope.launch { getAllSat() }
            1 -> viewModelScope.launch { getFavoriteSat() }
        }
    }
    override fun onTabUnselected(tab: TabLayout.Tab?) {}
    override fun onTabReselected(tab: TabLayout.Tab?) {}
}
