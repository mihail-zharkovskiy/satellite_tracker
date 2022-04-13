package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.SatelliteRepository
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.Satellite
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_position.SatellitePositionUseCase
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.UpdateUserLocationState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.UserLocationSource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.mapper.toMapSatUiData
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.MapUiData
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
import javax.inject.Inject

/**первым делом при создании всегда вызывай [initViewModel]**/
@HiltViewModel
class MapViewModel @Inject constructor(
    private val userLocationSource: UserLocationSource,
    private val repository: SatelliteRepository,
    private val satPositionUseCase: SatellitePositionUseCase,
    private val resource: Resource,
) : ViewModel() {

    private var renderSatellitesDataJob: Job? = null
    private val renderDelay: Long = 1000

    private var selectedSat: Satellite? = null
    private var satellites: List<Satellite> = emptyList()
    private var userLoc = Coordinates(0.0, 0.0)

    private val _dataState = MutableStateFlow<DataState<MapUiData>>(DataState.loading())
    val dataState: StateFlow<DataState<MapUiData>> get() = _dataState.asStateFlow()

    private val _userLocationState =
        MutableStateFlow<UpdateUserLocationState>(UpdateUserLocationState.Loading)
    val userLocationState: StateFlow<UpdateUserLocationState> get() = _userLocationState.asStateFlow()

    fun initViewModel(idSatellite: Int? = null) {
        updateUserLocation()
        viewModelScope.launch {
            getSatellites().join()
            getSelectedSat(satellites, idSatellite).join()
        }
    }

    fun clickOnSatellite(idSatellite: Int) = viewModelScope.launch {
        _dataState.value = DataState.loading()
        getSelectedSat(satellites, idSatellite)
    }

    fun updateUserLocation() {
        when (val updateState = userLocationSource.updateUserLocation()) {
            is UpdateUserLocationState.Success -> {
                userLoc = updateState.coordinate
                _userLocationState.value = userLocationSource.updateUserLocation()
            }
            else -> _userLocationState.value = updateState
        }
    }

    fun switchToSatelliteNext() {
        if (satellites.isNotEmpty()) {
            val index = satellites.indexOf(selectedSat)
            if (index < satellites.size - 1) {
                selectedSat = satellites[index + 1]
                renderSatellitesData()
            } else {
                selectedSat = satellites[0]
                renderSatellitesData()
            }
        }
    }

    fun switchToSatelliteBack() {
        if (satellites.isNotEmpty()) {
            val index = satellites.indexOf(selectedSat)
            if (index > 0) {
                selectedSat = satellites[index - 1]
                renderSatellitesData()
            } else {
                selectedSat = satellites[satellites.size - 1]
                renderSatellitesData()
            }
        }
    }

    private fun getSatellites() = viewModelScope.launch {
        val data = repository.getSelectedSatellitesForCalculation()
        if (data.isNotEmpty()) satellites = data else emptyList<Satellite>()
    }

    private fun getSelectedSat(satellites: List<Satellite>, idSat: Int?) = viewModelScope.launch {
        when {
            satellites.isNotEmpty() && idSat == null -> {
                selectedSat = satellites.first()
                renderSatellitesData()
            }
            satellites.isNotEmpty() && idSat != null -> {
                selectedSat = findSelectedSatInData(idSat, satellites)
                renderSatellitesData()
            }
            else -> _dataState.value = DataState.empty()
        }
    }

    private fun renderSatellitesData() = viewModelScope.launch {
        _dataState.value = DataState.loading()
        renderSatellitesDataJob?.cancelAndJoin()
        renderSatellitesDataJob = launch {
            while (isActive) {
                val date = Date()
                val satTrack = getTrackSat(selectedSat!!,
                    date) //TODO(#2 зуб даю не null.позже обязательно поправлю)
                val satellite = getDataSatellite(selectedSat!!,
                    date) //TODO(#1 зуб даю не null.позже обязательно поправлю)
                val satellites = getDataSatellites(satellites, date)
                val mapDataUiState = DataState.success(MapUiData(
                    satellites = satellites,
                    satTrack = satTrack,
                    satData = satellite,
                    satFootprint = Coordinates(satellite.latitude, satellite.longitude)
                ))
                _dataState.value = mapDataUiState
                delay(renderDelay)
            }
        }
    }

    private fun findSelectedSatInData(idSatellite: Int, satellites: List<Satellite>): Satellite {
        return satellites.find { it.tle.idSatellite == idSatellite } ?: satellites.first()
    }

    private suspend fun getTrackSat(sat: Satellite, date: Date): List<Coordinates> {
        val startDate = date.time
        val endDate = Date(date.time + (sat.orbitalPeriod * 2.4 * 20000L).toLong()).time
        return satPositionUseCase.getTrackSatellite(sat, userLoc, startDate, endDate)
    }

    private suspend fun getDataSatellites(sats: List<Satellite>, date: Date) = sats.map { sat ->
        satPositionUseCase.getPositionSat(sat, userLoc, date.time).toMapSatUiData(resource)
    }

    private suspend fun getDataSatellite(sat: Satellite, date: Date) =
        satPositionUseCase.getPositionSat(sat, userLoc, date.time).toMapSatUiData(resource)
}


