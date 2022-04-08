package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserUseCase
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.CompassEvent
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.PermissionState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.Status
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.Compass
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.UserLocationSource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.mapper.mapToSatAboveTheUserUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.model.SatAboveTheUserUiModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SatAboveTheUserViewModel @Inject constructor(
    private val userLocation: UserLocationSource,
    private val compass: Compass,
    private val satAboveTheUserUseCase: SatAboveTheUserUseCase,
    private val resource: Resource,
) : ViewModel(), Compass.AzimuthListener {

    private val _uiState =
        MutableStateFlow<DataState<List<SatAboveTheUserUiModel>>>(DataState.empty())
    val uiState: StateFlow<DataState<List<SatAboveTheUserUiModel>>> = _uiState

    private val _compassEvent = MutableStateFlow<CompassEvent>(CompassEvent.NoData)
    val compassEvent: StateFlow<CompassEvent> get() = _compassEvent

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.YesPermission)
    val permissionStat: StateFlow<PermissionState> get() = _permissionState

    init {
        viewModelScope.launch {
            satAboveTheUserUseCase.satAboveTheUser.collect { state ->
                when (state.status) {
                    Status.SUCCESS -> {
                        _uiState.value = DataState.succes(state.data!!.map {
                            it.mapToSatAboveTheUserUiModel(resource)
                        })
                    }
                    Status.LOADING -> {
                        _uiState.value = DataState.loading()
                    }
                    Status.EMPTY -> {
                        _uiState.value = DataState.empty()
                    }
                    Status.ERROR -> {}
                }
            }
        }
    }

    fun trigerEvent(userEvent: SatAboveTheUserEvent) {
        when (userEvent) {
            is SatAboveTheUserEvent.RefreshData -> {
                userLocation.updateUserLocation()
                calculateData()
            }
            is SatAboveTheUserEvent.OnResume -> {
                compass.startListening(this)
                chekLocationPermission()
                calculateData()
            }
            is SatAboveTheUserEvent.OnPause -> {
                compass.stopListening(this)
                viewModelScope.cancel()
            }
        }
    }

    override fun onOrientationChanged(compassEvent: CompassEvent) {
        _compassEvent.value = compassEvent
    }

    private fun chekLocationPermission() {
        _permissionState.value = userLocation.checkPermission()
    }

    private fun calculateData() {
        viewModelScope.launch {
            satAboveTheUserUseCase.calculateData(/*viewModelScope*/)
        }
    }

    override fun onCleared() {
        super.onCleared()
        //НЕ ВЫЗЫВАЕТСЯ ПРИ ПЕРЕХОДЕ НА ДРУШОЙ ЭКРАН
    }
}




