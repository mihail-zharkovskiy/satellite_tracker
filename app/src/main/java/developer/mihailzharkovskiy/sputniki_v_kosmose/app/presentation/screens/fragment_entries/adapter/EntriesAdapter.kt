package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_entries.model.EntriesUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.ItemEntryBinding

class EntriesAdapter(private val clickListener: EntriesClickListener) :
    RecyclerView.Adapter<EntriesAdapter.SatItemHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<EntriesUiModel>() {
        override fun areItemsTheSame(
            oldDomainModel: EntriesUiModel,
            newDomainModel: EntriesUiModel,
        ): Boolean {
            return oldDomainModel.idSatellite == newDomainModel.idSatellite
        }

        override fun areContentsTheSame(
            oldDomainModel: EntriesUiModel,
            newDomainModel: EntriesUiModel,
        ): Boolean {
            return oldDomainModel.isSelected == newDomainModel.isSelected
        }

        override fun getChangePayload(oldItem: EntriesUiModel, newItem: EntriesUiModel): Boolean? {
            return if (oldItem.idSatellite == newItem.idSatellite) newItem.isSelected
            else null
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    fun submitList(domainModels: List<EntriesUiModel>) {
        differ.submitList(domainModels)
    }

    override fun getItemCount(): Int = differ.currentList.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SatItemHolder {
        return SatItemHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SatItemHolder, position: Int) {
        holder.update(differ.currentList[position], clickListener)
    }

    override fun onBindViewHolder(holder: SatItemHolder, position: Int, payloads: List<Any?>) {
        if (payloads.isEmpty() || payloads[0] == null) {
            holder.update(differ.currentList[position], clickListener)
        } else holder.updatePayloads(differ.currentList[position],
            clickListener,
            payloads[0] as Boolean)
    }

    class SatItemHolder private constructor(private val binding: ItemEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun updatePayloads(
            model: EntriesUiModel,
            listener: EntriesClickListener,
            isSelected: Boolean,
        ) {
            binding.entryCheckbox.isChecked = isSelected
            itemView.setOnClickListener {
                listener.clickOnItemAdapter(model.idSatellite, model.isSelected.not())
            }
        }

        fun update(model: EntriesUiModel, listener: EntriesClickListener) {
            binding.entryCheckbox.text = model.nameSatellite
            binding.entryCheckbox.isChecked = model.isSelected
            itemView.setOnClickListener {
                listener.clickOnItemAdapter(model.idSatellite, model.isSelected.not())
            }
        }

        companion object {
            fun from(parent: ViewGroup): SatItemHolder {
                val inflater = LayoutInflater.from(parent.context)
                val binding = ItemEntryBinding.inflate(inflater, parent, false)
                return SatItemHolder(binding)
            }
        }
    }

    interface EntriesClickListener {
        fun clickOnItemAdapter(idSatellites: Int, isSelected: Boolean)
    }
}
