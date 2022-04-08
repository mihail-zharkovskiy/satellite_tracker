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
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.MapSatUiData
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.MapUiData
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.convertLatitudeForMap
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.convertLongitudeForMap
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

    private var updateJob: Job? = null
    private val delayUpdateData: Long = 1000

    private var selectedSat: Satellite? = null
    private var satellites: List<Satellite> = emptyList()
    private var userLocation = Coordinates(0.0, 0.0)

    private val _uiState = MutableStateFlow<DataState<MapUiData>>(DataState.loading())
    val uiState: StateFlow<DataState<MapUiData>> get() = _uiState.asStateFlow()

    private val _userLocationState =
        MutableStateFlow<UpdateUserLocationState>(UpdateUserLocationState.Loading)
    val userLocationState: StateFlow<UpdateUserLocationState> get() = _userLocationState.asStateFlow()

    fun initViewModel(idSatellite: Int? = null) {
        updateUserLocation()
        viewModelScope.launch {
            getSatellites()//.join()
            getSelectedSat(satellites, idSatellite)//.join()
        }
    }

    fun clickOnSatellite(idSatellite: Int) = viewModelScope.launch {
        _uiState.value = DataState.loading()
        getSelectedSat(satellites, idSatellite)
    }

    fun updateUserLocation() {
        when (val result = userLocationSource.updateUserLocation()) {
            is UpdateUserLocationState.Success -> {
                userLocation = Coordinates(result.coordinate.latitude, result.coordinate.longitude)
                _userLocationState.value = UpdateUserLocationState.Success(userLocation)
            }
            is UpdateUserLocationState.Error -> {
                _userLocationState.value = UpdateUserLocationState.Error(result.message)
            }
            is UpdateUserLocationState.Loading -> {
                _userLocationState.value = UpdateUserLocationState.Loading
            }
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
                renderSatellitesData(delayUpdateData)
            } else {
                selectedSat = satellites[satellites.size - 1]
                renderSatellitesData(delayUpdateData)
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
            else -> _uiState.value = DataState.empty()
        }
    }

    private fun renderSatellitesData(delayUpdate: Long = 500) = viewModelScope.launch {
        _uiState.value = DataState.loading()
        updateJob?.cancelAndJoin()
        updateJob = launch {
            while (isActive) {
                val date = Date()
                val satData =
                    getDataSat(selectedSat!!, date) //зуб даю не null. позже обязательно поправлю
                val satTrack =
                    getTrackSat(selectedSat!!, date) //зуб даюне null. позже обязательно поправлю
                val positions = getPositionsSat(satellites, date)
                delay(delayUpdate)
                val mapDataUiState = DataState.success(MapUiData(
                    satellites = positions,
                    satTrack = satTrack,
                    satData = satData,
                    satFootprint = Coordinates(satData.latitude, satData.longitude)
                ))
                _uiState.value = mapDataUiState
            }
        }
    }

    private fun findSelectedSatInData(idSatellite: Int, satellites: List<Satellite>): Satellite {
        return satellites.find { it.tle.idSatellite == idSatellite } ?: satellites.first()
    }

    private suspend fun getTrackSat(sat: Satellite, date: Date): List<Coordinates> {
        val startDate = date.time
        val endDate = Date(date.time + (sat.orbitalPeriod * 2.4 * 20000L).toLong()).time
        val trackSatellite =
            satPositionUseCase.getTrackSatellite(sat, Coordinates(0.0, 0.0), startDate, endDate)
                .map { satPos ->
                    val lat = convertLatitudeForMap(Math.toDegrees(satPos.latitude))
                    val lon = convertLongitudeForMap(Math.toDegrees(satPos.longitude))
                    Coordinates(lat, lon)
                }
        return trackSatellite
    }

    private suspend fun getPositionsSat(sats: List<Satellite>, date: Date): List<MapSatUiData> {
        return sats.map { sat ->
            satPositionUseCase.getPositionSat(sat, Coordinates(0.0, 0.0), date.time)
                .toMapSatUiData(resource)
        }
    }

    private suspend fun getDataSat(sat: Satellite, date: Date): MapSatUiData {
        return satPositionUseCase.getPositionSat(sat, Coordinates(0.0, 0.0), date.time)
            .toMapSatUiData(resource)
    }
}


