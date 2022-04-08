package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding
import developer.mihailzharkovskiy.sputniki_v_kosmose.R

abstract class BaseDialogFragment<B : ViewBinding> : DialogFragment() {

    protected abstract fun initBinding(inflater: LayoutInflater, container: ViewGroup?): B

    private var _binding: B? = null
    protected val binding: B get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        dialog?.window?.setBackgroundDrawableResource(R.color.prozrachniy)
        _binding = initBinding(inflater, container)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.attributes?.width = resources.getDimension(R.dimen.dialog_width).toInt()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}