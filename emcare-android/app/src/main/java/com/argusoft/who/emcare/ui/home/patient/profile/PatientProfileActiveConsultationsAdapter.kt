package com.argusoft.who.emcare.ui.home.patient.profile

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.databinding.ListItemActiveConsultationBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.ActiveConsultationData
import com.argusoft.who.emcare.utils.extention.toBinding

class PatientProfileActiveConsultationsAdapter(
    val list: ArrayList<ActiveConsultationData?> = arrayListOf(),
) : BaseAdapter<ActiveConsultationData>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is PatientProfileActiveConsultationsAdapter.ViewHolder -> {
                list[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemActiveConsultationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ActiveConsultationData) = with(item) {
            binding.consultationNameTextView.text = consultationLabel
            binding.dateTextView.text = visitDate
        }
    }

}