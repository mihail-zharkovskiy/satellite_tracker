package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.CompassEvent
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.base.BaseFragment
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.extention.toast
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.Status
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.framework.Compass
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_map.MapFragment
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.model.SatelliteDataUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.state.SatelliteDataUiState
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.FragmentSatelliteDataBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class SatelliteDataFragment : BaseFragment<FragmentSatelliteDataBinding>() {

    @Inject
    lateinit var compass: Compass

    //    private val stringUriSearch by lazy { requireContext().getString(R.string.dsf_uri_search_sputnik) }
    private val viewModel: SatelliteDataViewModel by viewModels()

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSatelliteDataBinding {
        return FragmentSatelliteDataBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initScreen()
        observeStates()
    }

    override fun onResume() {
        super.onResume()
        compass.startListening(viewModel)
        binding.progressOrbita.starAnimation()
    }

    override fun onPause() {
        super.onPause()
        compass.stopListening(viewModel)
        binding.progressOrbita.stopAnimation()
    }

    private fun initScreen() {
        arguments?.getParcelable<SatAboveTheUserDomainModel>(KEY_SPUTNIK)?.let { satellite ->
            val data = viewModel.initLaunchDataForScreen(satellite)
            binding.tvSputnikNazvanie.text = data.satName
            binding.tvSputnikAzimutEnd.text = data.azimuthEnd
            binding.tvSputnikAzimutStart.text = data.azimuthStart
            binding.ivBack.setOnClickListener { findNavController().popBackStack() }
            binding.btnPosmotretNaKarte.setOnClickListener { seeOnTheMap(satellite) }
            viewModel.sendPassData(satellite)
        } ?: requireContext().toast("no data")

        binding.btnInformOSptnike.setOnClickListener { getInfoFromBrowser() }
    }

    private fun observeStates() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch { viewModel.compassEvent.collect { event -> applyCompasEvent(event) } }
                launch { viewModel.progress.collect { state -> applyUiState(state) } }
            }
        }
    }

    private fun applyCompasEvent(event: CompassEvent) {
        when (event) {
            is CompassEvent.Data -> {
                binding.tvKompasAzimut.text = event.azimuthString
                binding.viewKompasObvod.rotation = -event.azimuth.toFloat()
            }
            is CompassEvent.Error -> requireContext().toast(event.message)
            is CompassEvent.NoData -> {}
        }
    }


    private fun applyUiState(state: SatelliteDataUiState) {
        when (state) {
            is SatelliteDataUiState.SatVisible -> {
                applyDataState(state.data)
                binding.progressOrbita.progress = state.progress
                binding.progressOrbita.visibleSatellites = true
                binding.tvSatelliteInvisible.visibility = View.INVISIBLE
            }
            is SatelliteDataUiState.SatInvisible -> {
                applyDataState(state.data)
                binding.progressOrbita.visibleSatellites = false
                binding.tvSatelliteInvisible.visibility = View.VISIBLE
            }
            is SatelliteDataUiState.NoData -> {}
        }
    }


    private fun applyDataState(state: DataState<SatelliteDataUiModel>) = when (state.status) {
        Status.SUCCESS -> {
            val data = state.data
            if (data != null) {
                binding.tvSputnikVisota.text = data.altitude
                binding.tvSputnikSkorost.text = data.velocity
                binding.tvSputnikElevation.text = data.elevation
                binding.tvSputnikUdalenieOtUser.text = data.range
                binding.tvSputnikAzimutCuration.text = data.azimuth
            } else {
                requireContext().toast("error")
            }
        }
        Status.LOADING -> {}
        Status.EMPTY -> {}
        Status.ERROR -> {}
    }

    private fun seeOnTheMap(data: SatAboveTheUserDomainModel) {
        findNavController().navigate(
            R.id.action_danieSputnikaFragment_to_nav_map,
            bundleOf(MapFragment.KEY_SPUTNIK to data.satId)
        )
    }

    private fun getInfoFromBrowser() {
//        val uri = Uri.parse("$stringUriSearch ${binding.tvSputnikNazvanie.text}")
        val uri = Uri.parse(
            "${getString(R.string.dsf_uri_search_sputnik)} ${binding.tvSputnikNazvanie.text}"
        )
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        startActivity(intent)
    }

    companion object {
        const val KEY_SPUTNIK = "KEY_SPUTNIK"
    }
}

