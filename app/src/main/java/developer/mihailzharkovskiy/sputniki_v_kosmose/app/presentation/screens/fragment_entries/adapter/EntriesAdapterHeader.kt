package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.ItemEntryHeaderBinding

class EntriesAdapterHeader : RecyclerView.Adapter<EntriesAdapterHeader.HeaderViewHolder>() {

    var sum: Int = 0
        set(value) {
            if (field != value) {
                field = value
                notifyItemChanged(0)
            }
        }

    inner class HeaderViewHolder(val binding: ItemEntryHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {

        private val stringSumSatellites = itemView.context.getString(R.string.ef_heder_fragment)

        fun bindMy() {
            binding.zagolovok.text = String.format(stringSumSatellites, sum.toString())
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeaderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HeaderViewHolder(ItemEntryHeaderBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: HeaderViewHolder, position: Int) {
        holder.bindMy()
    }

    override fun getItemCount(): Int {
        return 1
    }
}