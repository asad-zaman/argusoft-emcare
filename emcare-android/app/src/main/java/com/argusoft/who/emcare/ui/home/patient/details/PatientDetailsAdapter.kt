package com.argusoft.who.emcare.ui.home.patient.details

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.databinding.PatientDetailsItemBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.PatientItemData
import com.argusoft.who.emcare.utils.extention.toBinding

class PatientDetailsAdapter(
    val list: ArrayList<PatientItemData?> = arrayListOf()
) : BaseAdapter<PatientItemData>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is PatientDetailsAdapter.ViewHolder -> {
                list[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: PatientDetailsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PatientItemData) = with(item) {
            binding.headerTextView.text = header
            binding.valueTextView.text = value
        }
    }

}