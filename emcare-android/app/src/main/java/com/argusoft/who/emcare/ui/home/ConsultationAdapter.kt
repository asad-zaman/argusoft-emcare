package com.argusoft.who.emcare.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.databinding.ListItemConsultationBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.utils.extention.toBinding

class ConsultationAdapter(
    val list: ArrayList<ConsultationItemData?> = arrayListOf()
) : BaseAdapter<ConsultationItemData>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is ViewHolder -> {
                list[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemConsultationBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: ConsultationItemData) = with(album) {
            binding.nameTextView.text = name
            binding.dateOfBirthValueTextView.text = dateOfBirth
            binding.consultationDateValueTextView.text = dateOfConsultation
            binding.badgeTextView.text = badgeText
            binding.rightConsultationImageView.setImageResource(consultationIcon!!)
        }
    }
}