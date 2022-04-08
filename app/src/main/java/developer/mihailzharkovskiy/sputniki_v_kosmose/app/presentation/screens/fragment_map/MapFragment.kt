package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.UpdateUserLocationState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.base.BaseFragment
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.extention.showSnackBarAlarm
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.Status
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.map_view_wrapper.MapViewWrapper
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.MapSatUiData
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.model.MapUiData
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.FragmentMapBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MapFragment : BaseFragment<FragmentMapBinding>(), MapViewWrapper.ClickOnMarkerMapListener {

    private var map: MapViewWrapper? = null
    private var snackBarAlarm: Snackbar? = null
    private val viewModel: MapViewModel by lazy {
        val viewModel: MapViewModel by viewModels()
        viewModel.initViewModel(arguments?.getInt(KEY_SPUTNIK))
        viewModel
    }


    override fun initBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMapBinding {
        return FragmentMapBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        map = MapViewWrapper(binding.mapView, this)

//        viewModel.initViewModel(arguments?.getInt(KEY_SPUTNIK))

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.uiState.collect { state -> applyMapUiState(state) } }
                launch { viewModel.userLocationState.collect { state -> applyUserLocationState(state) } }
            }
        }

        with(binding) {
            btUpdateLocation.setOnClickListener { viewModel.updateUserLocation() }
            mapBtnBack.setOnClickListener { viewModel.pereklchitsyaNaSptnikNazad() }
            mapBtnNext.setOnClickListener { viewModel.pereklchitsyaNaSptnikVpered() }
            mapDataName.setOnClickListener { getInformFromBrowser() }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.tabLayout.addOnTabSelectedListener(tabLayoutListener)
    }

    override fun onPause() {
        super.onPause()
        snackBarAlarm?.dismiss()
        snackBarAlarm = null
        binding.tabLayout.removeOnTabSelectedListener(tabLayoutListener)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        map = null
    }

    override fun clickOnMarkerMap(idSatellite: Int) {
        viewModel.clickOnSatellite(idSatellite)
    }

    private fun applyUserLocationState(state: UpdateUserLocationState) {
        when (state) {
            is UpdateUserLocationState.Success -> {
                map?.invalidateUserLocation(state.coordinate)
            }
            is UpdateUserLocationState.Error -> {
                snackBarAlarm =
                    showSnackBarAlarm(state.message, binding.root, binding.btUpdateLocation)
            }
            is UpdateUserLocationState.Loading -> {}
        }
    }

    private fun applyMapUiState(state: DataState<MapUiData>) {
        when (state.status) {
            Status.SUCCESS -> {
                binding.progressBar.visibility = View.GONE
                binding.mapDataName.isEnabled = true
                if (state.data != null) {
                    renderSatellitesOnMap(state.data)
                    renderSateliteData(state.data.satData)
                }
            }
            Status.LOADING -> {
                binding.tvAlarm.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
                binding.mapDataName.isEnabled = false
            }
            Status.EMPTY -> {
                binding.progressBar.visibility = View.GONE
                binding.tvAlarm.visibility = View.VISIBLE
                binding.mapDataName.isEnabled = false
            }
            Status.ERROR -> {}
        }
    }

    private fun renderSatellitesOnMap(data: MapUiData) {
        map?.invalidateSatellites(
            data.satellites,
            data.satTrack,
            data.satFootprint,
        )
    }

    private fun renderSateliteData(data: MapSatUiData) {
        binding.apply {
            mapDataName.text = data.name
            mapDataAlt.text = data.altitude
            mapDataDst.text = data.range
            mapDataVel.text = data.velocity
            mapDataLat.text = data.latitudeString
            mapDataLon.text = data.longitudeString
        }
    }

    private fun getInformFromBrowser() {
        val uri =
            Uri.parse(String.format(requireContext().getString(R.string.dsf_uri_search_sputnik),
                binding.mapDataName.text))
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        startActivity(intent)
    }

    private val tabLayoutListener = object : TabLayout.OnTabSelectedListener {
        override fun onTabSelected(tab: TabLayout.Tab?) {
            when (tab?.position) {
                0 -> map?.showIcon = true
                1 -> map?.showIcon = false
            }
        }

        override fun onTabUnselected(tab: TabLayout.Tab?) {}
        override fun onTabReselected(tab: TabLayout.Tab?) {}
    }

    companion object {
        const val KEY_SPUTNIK = "catNum"
    }
}
