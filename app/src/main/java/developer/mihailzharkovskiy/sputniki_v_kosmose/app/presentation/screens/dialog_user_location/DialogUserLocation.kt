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

    //    private val permFineLoc = Manifest.permission.ACCESS_FINE_LOCATION
    private val permCoarseLoc = Manifest.permission.ACCESS_COARSE_LOCATION
    private val REQUEST_LOCATION_PERMISSON = 1

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
                arrayOf(/*permFineLoc,*/ permCoarseLoc), REQUEST_LOCATION_PERMISSON
            )
            dialog?.cancel()
        }
    }

    companion object {
        const val TAG_DUL = "TAG_DUL"
        fun show(supportFragmentManager: FragmentManager) {
            DialogUserLocation().show(supportFragmentManager, TAG_DUL)
        }
    }
}