package developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import developer.mihailzharkovskiy.sputniki_v_kosmose.R
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.domain.usecase.satellite_above_the_user.SatAboveTheUserDomainModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.app.presentation.screens.fragment_satellite_above_the_user.model.SatAboveTheUserUiModel
import developer.mihailzharkovskiy.sputniki_v_kosmose.databinding.ItemSatAboveTheUserMainBinding

class SatAboveTheUserAdapterMain(
    private val clickListener: ClickItemListener,
) : RecyclerView.Adapter<SatAboveTheUserAdapterMain.SatPassLeoHolder>() {

    fun submitList(passes: List<SatAboveTheUserUiModel>) {
        differ.submitList(passes)
    }

    private val diffCallback = object : DiffUtil.ItemCallback<SatAboveTheUserUiModel>() {
        override fun areItemsTheSame(
            oldItem: SatAboveTheUserUiModel,
            newItem: SatAboveTheUserUiModel,
        ): Boolean {
            return oldItem.satId == newItem.satId && oldItem.startTime == newItem.startTime
        }

        override fun areContentsTheSame(
            oldItem: SatAboveTheUserUiModel,
            newItem: SatAboveTheUserUiModel,
        ): Boolean {
            //oldItem.progress == newItem.progress почемуо возращает true кода прогрес на самом деле разный
            return oldItem.progress != newItem.progress
        }

        override fun getChangePayload(
            oldItem: SatAboveTheUserUiModel,
            newItem: SatAboveTheUserUiModel,
        ): Int? {
            return if (oldItem.satId == newItem.satId) newItem.progress
            else null
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SatPassLeoHolder {
        return SatPassLeoHolder.from(parent)
    }

    override fun onBindViewHolder(holder: SatPassLeoHolder, position: Int) {
        holder.update(differ.currentList[position], clickListener)
    }

    override fun onBindViewHolder(holder: SatPassLeoHolder, position: Int, payloads: List<Any?>) {
        if (payloads.isEmpty() || payloads[0] == null) {
            holder.update(differ.currentList[position], clickListener)
        } else holder.updatePayloads(payloads[0] as Int)
    }

    class SatPassLeoHolder private constructor(private val binding: ItemSatAboveTheUserMainBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun updatePayloads(progrss: Int) {
            binding.progress.progress = progrss
        }

        fun update(
            data: SatAboveTheUserUiModel,
            clickListener: ClickItemListener,
        ) {
            binding.apply {
                passLeoName.text = data.name
                when (data.isDeepSpace) {
                    false -> ivOrbita.setImageResource(R.drawable.ic_sat_okolozemnaya_orbita)
                    true -> ivOrbita.setImageResource(R.drawable.ic_sat_geostacionarnaya_orbita)
                }
                progress.progress = data.progress
                tvTimeEnd.text = data.endTime
                tvTimeStart.text = data.startTime
                tvMaxElev.text = data.maxElevation
                tvAzimuthEnd.text = data.endAzimuth
                tvAzimuthStart.text = data.startAzimuth
                tvAzimuthMaxElev.text = data.centerAzimuth
            }
            itemView.setOnClickListener {
                clickListener.onClick(data.satAboveTheUserDomainModel)
            }
        }

        companion object {
            fun from(parent: ViewGroup): SatPassLeoHolder {
                val inflater = LayoutInflater.from(parent.context)
                return SatPassLeoHolder(ItemSatAboveTheUserMainBinding.inflate(inflater,
                    parent,
                    false))
            }
        }
    }

    override fun getItemCount() = differ.currentList.size

    override fun getItemViewType(position: Int): Int {
        return if (differ.currentList[position].isDeepSpace) 1
        else 0
    }

    interface ClickItemListener {
        fun onClick(satellite: SatAboveTheUserDomainModel)
    }
}
