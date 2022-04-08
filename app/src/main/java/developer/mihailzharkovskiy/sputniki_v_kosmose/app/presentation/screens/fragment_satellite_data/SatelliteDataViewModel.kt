package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.Coordinates
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.core_calculations.predict.Satellite
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_position.SatellitePositionUseCase
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.CompassEvent
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.Compass
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.UserLocationSource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.mapper.mapToInitUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.mapper.mapToSatDataUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model.SatelliteDataInitUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model.SatelliteDataUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.state.SatelliteDataUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SatelliteDataViewModel @Inject constructor(
    private val compas: Compass,
    private val suserLocation: UserLocationSource,
    private val satPositionUseCase: SatellitePositionUseCase,
    private val resource: Resource,
) : ViewModel(), Compass.AzimuthListener {

    private val userPos = suserLocation.getUserLocation()

    private val _progress = MutableStateFlow<SatelliteDataUiState>(SatelliteDataUiState.NoData)
    val progress: StateFlow<SatelliteDataUiState> get() = _progress.asStateFlow()

    private val _compassEvent = MutableStateFlow<CompassEvent>(CompassEvent.NoData)
    val compassEvent: StateFlow<CompassEvent> = _compassEvent


    fun initLaunchDataForScreen(satellite: SatAboveTheUserDomainModel): SatelliteDataInitUiModel {
        return satellite.mapToInitUiModel(resource)
    }

    private fun calculateProgress(timeStart: Long, losTime: Long): Float {
        val timeNow = System.currentTimeMillis()
        val deltaNow = timeNow.minus(timeStart).toFloat()
        val deltaTotal = losTime.minus(timeStart).toFloat()
        return ((deltaNow / deltaTotal) * 100)
    }

    private suspend fun getSatData(
        sat: Satellite,
        userPos: Coordinates,
        date: Date,
    ): SatelliteDataUiModel {
        val satPos = satPositionUseCase.getPositionSat(sat, userPos, date.time)
        return satPos.mapToSatDataUiModel(resource)
    }

    fun sendPassData(satPass: SatAboveTheUserDomainModel) {
        viewModelScope.launch {
            while (isActive) {
                val data = getSatData(satPass.satellite, userPos, Date())
                val progress = calculateProgress(satPass.startTime, satPass.endTime)
                val state = if (progress.toInt() in 1 until 100) {
                    SatelliteDataUiState.SatVisible(progress, DataState.succes(data))
                } else {
                    SatelliteDataUiState.SatInvisible(DataState.succes(data))
                }
                _progress.value = state
                delay(1000)
            }
        }
    }

    override fun onOrientationChanged(compassEvent: CompassEvent) {
        _compassEvent.value = compassEvent
    }
}

