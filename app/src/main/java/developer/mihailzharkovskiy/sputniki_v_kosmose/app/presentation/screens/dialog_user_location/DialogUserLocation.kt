package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.dialog_user_location

import android.Manifest
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentManager
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.base.BaseDialogFragment
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.DialogUserLocationBinding

class DialogUserLocation : BaseDialogFragment<DialogUserLocationBinding>() {

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): DialogUserLocationBinding {
        return DialogUserLocationBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvOnPermissionLocation.setOnClickListener {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permCoarseLoc), REQUEST_LOCATION_PERMISSION
            )
            dialog?.cancel()
        }
    }

    companion object {
        private const val permCoarseLoc = Manifest.permission.ACCESS_COARSE_LOCATION
        private const val REQUEST_LOCATION_PERMISSION = 1
        private const val TAG_DUL = "TAG_DUL"

        fun show(supportFragmentManager: FragmentManager) {
            DialogUserLocation().show(supportFragmentManager, TAG_DUL)
        }
    }
}