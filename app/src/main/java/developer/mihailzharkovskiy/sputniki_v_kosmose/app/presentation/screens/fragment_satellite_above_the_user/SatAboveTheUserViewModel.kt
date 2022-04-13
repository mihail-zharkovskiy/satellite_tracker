package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserUseCase
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.CompassEvent
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.PermissionState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.UpdateUserLocationState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.resource.Resource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.Status
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.Compass
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.UserLocationSource
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.mapper.toSatAboveTheUserUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.model.SatAboveTheUserUiModel
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    private val _compassEvent = MutableStateFlow<CompassEvent>(CompassEvent.NoData)
    val compassEvent: StateFlow<CompassEvent> get() = _compassEvent.asStateFlow()

    private val _permissionState = MutableStateFlow<PermissionState>(PermissionState.YesPermission)
    val permissionStat: StateFlow<PermissionState> get() = _permissionState.asStateFlow()

    private val _dataState =
        MutableStateFlow<DataState<List<SatAboveTheUserUiModel>>>(DataState.loading())
    val dataState: StateFlow<DataState<List<SatAboveTheUserUiModel>>> = _dataState.asStateFlow()

    init {
        calculateData()
        viewModelScope.launch {
            satAboveTheUserUseCase.satAboveTheUser.collect { state ->
                when (state.status) {
                    Status.SUCCESS -> {
                        /**здесь data точно не null, смотри [DataState.success]**/
                        val data = state.data!!.map { it.toSatAboveTheUserUiModel(resource) }
                        _dataState.value = DataState.success(data)
                    }
                    Status.LOADING -> _dataState.value = DataState.loading()
                    Status.EMPTY -> _dataState.value = DataState.empty()
                    Status.ERROR -> _dataState.value = DataState.error(state.message)
                }
            }
        }
    }

    fun handleEvent(userEvent: SatAboveTheUserEvent) {
        when (userEvent) {
            is SatAboveTheUserEvent.RefreshData -> {
                calculateData()
            }
            is SatAboveTheUserEvent.OnResume -> {
                compass.startListening(this)
                chekLocationPermission()
//                calculateData()
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

    private fun calculateData() = viewModelScope.launch {
        when (val resultUpdate = userLocation.updateUserLocation()) {
            is UpdateUserLocationState.Success ->
                satAboveTheUserUseCase.calculateData(viewModelScope, resultUpdate.coordinate)
            is UpdateUserLocationState.Error ->
                satAboveTheUserUseCase.calculateData(viewModelScope,
                    resultUpdate.defaultCoordinates)
            is UpdateUserLocationState.Loading -> {}
        }
    }
}




