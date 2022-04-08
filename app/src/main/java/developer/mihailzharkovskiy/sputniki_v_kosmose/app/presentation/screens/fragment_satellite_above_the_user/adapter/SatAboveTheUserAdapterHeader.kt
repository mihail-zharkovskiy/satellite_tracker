package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.adapter

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.framework.user_location.PermissionState
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.ItemSatAboveTheUserHeaderBinding

class SatAboveTheUserAdapterHeader :
    RecyclerView.Adapter<SatAboveTheUserAdapterHeader.ViewHolderHeader>() {

    var state: PermissionState = PermissionState.YesPermission
        set(value) {
            if (field != value) {
                field = value
                notifyItemChanged(0)
            }
        }

    inner class ViewHolderHeader(val binding: ItemSatAboveTheUserHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun myBind() {
            when (state) {
                PermissionState.NoPermission -> {
                    binding.tvNoPermission.visibility = View.VISIBLE
                    binding.tvButtonVZavisimostiOtState.apply {
                        visibility = View.VISIBLE
                        setOnClickListener { openSettingApp(itemView) }
                    }
                }
                PermissionState.YesPermission -> {
                    binding.tvNoPermission.visibility = View.GONE
                    binding.tvButtonVZavisimostiOtState.visibility = View.GONE
                }
            }
        }
    }

    private fun openSettingApp(view: View) {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.data = Uri.parse("package:" + view.context.packageName)
        view.context.startActivity(intent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderHeader {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolderHeader(ItemSatAboveTheUserHeaderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolderHeader, position: Int) = holder.myBind()

    override fun getItemCount(): Int = 1
}