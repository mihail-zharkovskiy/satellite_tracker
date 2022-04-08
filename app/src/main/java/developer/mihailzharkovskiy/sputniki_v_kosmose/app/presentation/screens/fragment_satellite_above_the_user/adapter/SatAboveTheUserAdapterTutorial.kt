package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.ItemSatAboveTheUserTutorialBinding

class SatAboveTheUserAdapterTutorial :
    RecyclerView.Adapter<SatAboveTheUserAdapterTutorial.ViewHolderTutorial>() {

    var visibility = true
        set(value) {
            field = value
            notifyItemChanged(0)
        }

    inner class ViewHolderTutorial(val binding: ItemSatAboveTheUserTutorialBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            if (visibility) binding.root.visibility = View.VISIBLE
            else binding.root.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderTutorial {
        val inflate = LayoutInflater.from(parent.context)
        return ViewHolderTutorial(ItemSatAboveTheUserTutorialBinding.inflate(inflate,
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ViewHolderTutorial, position: Int) = holder.bind()

    override fun getItemCount(): Int = 1

}