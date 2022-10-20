package com.argusoft.who.emcare.ui.home.patient.profile

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.databinding.ListItemPreviousConsultationBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.PreviousConsultationData
import com.argusoft.who.emcare.utils.extention.toBinding

class PatientProfilePreviousConsultationsAdapter(
    val list: ArrayList<PreviousConsultationData?> = arrayListOf(),
) : BaseAdapter<PreviousConsultationData>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is PatientProfilePreviousConsultationsAdapter.ViewHolder -> {
                list[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemPreviousConsultationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PreviousConsultationData) = with(item) {
            binding.consultationNameTextView.text = consultationLabel
            binding.dateTextView.text = visitDate
        }
    }

}