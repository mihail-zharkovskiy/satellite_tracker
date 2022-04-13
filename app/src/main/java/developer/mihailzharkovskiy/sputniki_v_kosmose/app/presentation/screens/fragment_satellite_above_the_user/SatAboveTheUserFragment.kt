package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user

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
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dagger.hilt.android.AndroidEntryPoint
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.compas.CompassEvent
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.PermissionState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.base.BaseFragment
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.extention.toast
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.Status
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.adapter.SatAboveTheUserAdapterHeader
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.adapter.SatAboveTheUserAdapterMain
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.adapter.SatAboveTheUserAdapterTutorial
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.model.SatAboveTheUserUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_data.SatelliteDataFragment
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.FragmentSatAboveTheUserBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SatAboveTheUserFragment : BaseFragment<FragmentSatAboveTheUserBinding>(),
    SatAboveTheUserAdapterMain.ClickItemListener {

    private val mainAdapter by lazy { SatAboveTheUserAdapterMain(this) }
    private val headerAdapter by lazy { SatAboveTheUserAdapterHeader() }
    private val tutorialAdapter by lazy { SatAboveTheUserAdapterTutorial() }
    private val viewModel: SatAboveTheUserViewModel by viewModels()

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentSatAboveTheUserBinding {
        return FragmentSatAboveTheUserBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
        observeStates()
    }

    override fun onResume() {
        super.onResume()
        viewModel.handleEvent(SatAboveTheUserEvent.OnResume)
        binding.refreshLayout.setOnRefreshListener(swipeRefreshListener)
    }

    override fun onPause() {
        super.onPause()
        viewModel.handleEvent(SatAboveTheUserEvent.OnPause)
        binding.refreshLayout.setOnRefreshListener(null)
    }

    private val swipeRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        viewModel.handleEvent(SatAboveTheUserEvent.RefreshData)
        binding.refreshLayout.isRefreshing = false
    }

    private fun observeStates() = viewLifecycleOwner.lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            launch { viewModel.compassEvent.collect { state -> applyCompassEvent(state) } }
            launch { viewModel.dataState.collect { state -> applyDataState(state) } }
            launch { viewModel.permissionStat.collect { state -> applyPermissionState(state) } }
        }
    }

    private fun applyPermissionState(state: PermissionState) {
        when (state) {
            is PermissionState.NoPermission -> headerAdapter.state = PermissionState.NoPermission
            is PermissionState.YesPermission -> headerAdapter.state = PermissionState.YesPermission
        }
    }

    private fun applyCompassEvent(event: CompassEvent) {
        when (event) {
            is CompassEvent.Data -> {
                binding.tvKompasAzimut.text = event.azimuthString
                binding.viewRingCompas.rotation = -event.azimuth.toFloat()
            }
            is CompassEvent.Error -> requireContext().toast(event.message)
            is CompassEvent.NoData -> {}
        }
    }

    private fun applyDataState(state: DataState<List<SatAboveTheUserUiModel>>) {
        when (state.status) {
            Status.SUCCESS -> {
                binding.tvAlarm.visibility = View.GONE
                binding.progressBarRefresh.visibility = View.GONE
                binding.refreshLayout.isEnabled = true
                if (state.data != null) mainAdapter.submitList(state.data)
            }
            Status.LOADING -> {
                binding.tvAlarm.visibility = View.GONE
                binding.progressBarRefresh.visibility = View.VISIBLE
                binding.refreshLayout.isEnabled = false
            }
            Status.EMPTY -> {
                binding.tvAlarm.visibility = View.VISIBLE
                binding.tvAlarm.text = resources.getString(R.string.fps_now_no_prolet_sat)
                binding.progressBarRefresh.visibility = View.GONE
                binding.refreshLayout.isEnabled = false
                mainAdapter.submitList(emptyList())
            }
            Status.ERROR -> {
                binding.tvAlarm.visibility = View.VISIBLE
                binding.tvAlarm.text = resources.getString(R.string.fps_calculate_error)
                binding.progressBarRefresh.visibility = View.GONE
                binding.refreshLayout.isEnabled = false
            }
        }
    }

    private fun initRecyclerView() {
        binding.passesRecycler.apply {
            setHasFixedSize(true)
            adapter = ConcatAdapter(headerAdapter, tutorialAdapter, mainAdapter)
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onClick(satellite: SatAboveTheUserDomainModel) {
        findNavController().navigate(
            R.id.action_nav_passes_to_nav_danie_sputnika,
            bundleOf(SatelliteDataFragment.KEY_SPUTNIK to satellite)
        )
    }
}


