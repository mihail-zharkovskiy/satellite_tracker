package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.internet.InternetState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.internet.ReceiverInternetChanges
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.base.BaseFragment
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.extention.showSnackBarAlarm
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.extention.toast
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.DataState
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.data_state.Status
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.dialog_internet.InternetDialog
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.adapter.EntriesAdapter
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.adapter.EntriesAdapterHeader
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.model.EntriesUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.state.EntriesUiState
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.FragmentEntriesBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class EntriesFragment : BaseFragment<FragmentEntriesBinding>() {

    private val entriesAdapter: EntriesAdapter by lazy { EntriesAdapter(viewModel) }
    private val headerAdapter: EntriesAdapterHeader by lazy { EntriesAdapterHeader() }
    private val receiver: ReceiverInternetChanges by lazy { ReceiverInternetChanges(viewModel) }
    private val viewModel: EntriesViewModel by viewModels()
    private var snackBarAlarm: Snackbar? = null

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): FragmentEntriesBinding {
        return FragmentEntriesBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState
                .flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collect { state -> applyUiState(state) }
        }
    }

    override fun onResume() {
        super.onResume()
        receiver.registerReceiver(requireContext())
        binding.etSearch.addTextChangedListener(viewModel)
        binding.tabLayout.addOnTabSelectedListener(viewModel)
    }

    override fun onPause() {
        super.onPause()
        snackBarAlarm?.dismiss()
        snackBarAlarm = null
        receiver.unRegisterReceiver(requireContext())
        binding.etSearch.removeTextChangedListener(viewModel)
        binding.tabLayout.removeOnTabSelectedListener(viewModel)
    }

    private fun applyUiState(uiState: EntriesUiState) {
        when (uiState) {
            is EntriesUiState.SectionAllSat -> {
                applyDataState(uiState.state)
                binding.entriesSelectAll.setOnClickListener { viewModel.selectAllSatellites() }
            }
            is EntriesUiState.SectionFavoriteSat -> {
                applyDataState(uiState.state)
                binding.entriesSelectAll.setOnClickListener {
                    snackBarAlarm = showSnackBarAlarm(
                        alarmMessage = requireContext().getString(R.string.fe_toast),
                        anchorView = binding.cardView4,
                        rootView = binding.root
                    )
                }
            }
            is EntriesUiState.Internet -> applyInternetState(uiState.state)
            is EntriesUiState.Loading -> {}
        }
    }

    private fun applyInternetState(state: InternetState) = when (state) {
        is InternetState.Off -> binding.entriesImportWeb.setOnClickListener {
            InternetDialog.show(parentFragmentManager)
        }
        is InternetState.On -> binding.entriesImportWeb.setOnClickListener {
            viewModel.saveDataFromWeb()
        }
    }

    private fun applyDataState(state: DataState<List<EntriesUiModel>>) = when (state.status) {
        Status.SUCCESS -> {
            if (state.data != null) {
                entriesAdapter.submitList(state.data)
                headerAdapter.sum = state.data.size
            }
            binding.progressBar.visibility = View.GONE
            binding.tvAlarm.visibility = View.GONE
            binding.entriesRecycler.visibility = View.VISIBLE
            binding.entriesSelectAll.isEnabled = true
        }
        Status.EMPTY -> {
            binding.tvAlarm.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.entriesRecycler.visibility = View.INVISIBLE
            binding.entriesSelectAll.isEnabled = false
        }
        Status.LOADING -> {
            binding.tvAlarm.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            binding.entriesRecycler.visibility = View.INVISIBLE
            binding.entriesSelectAll.isEnabled = false
        }
        Status.ERROR -> {
            binding.tvAlarm.visibility = View.VISIBLE
            binding.entriesSelectAll.isEnabled = false
            requireContext().toast("error")
        }
    }

    private fun setupRecyclerView() {
        binding.entriesRecycler.apply {
            setHasFixedSize(true)
            /**ConcatAdapter.Config.Builder().setIsolateViewTypes(false).build() дабы коректно работал viewtype**/
            adapter = ConcatAdapter(headerAdapter, entriesAdapter)
            layoutManager = createGridLayoutManager()
            (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
        }
    }

    private fun createGridLayoutManager(): GridLayoutManager {
        /**в данном случа говорим что первый элемент будт занимать всю ширину ресайклер,
         *  а послудующие будут идти в два столбца**/
        return GridLayoutManager(requireContext(), 2).apply {
            this.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
                override fun getSpanSize(position: Int): Int = if (position == 0) 2 else 1
            }
        }
    }
}
