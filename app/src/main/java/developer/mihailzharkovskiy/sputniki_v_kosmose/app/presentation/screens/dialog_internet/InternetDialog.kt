package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.dialog_internet

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.common.base.BaseDialogFragment
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.DialogInternetBinding

class InternetDialog : BaseDialogFragment<DialogInternetBinding>() {

    override fun initBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
    ): DialogInternetBinding {
        return DialogInternetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvOnInternet.setOnClickListener { perehodVnastroykiInterneta() }
    }

    private fun perehodVnastroykiInterneta() {
        startActivity(Intent(Intent(Settings.ACTION_SETTINGS)))
    }

    companion object {
        const val TAG = "InternetDialog"
        fun show(fragmentManager: FragmentManager) {
            InternetDialog().show(fragmentManager, TAG)
        }
    }
}