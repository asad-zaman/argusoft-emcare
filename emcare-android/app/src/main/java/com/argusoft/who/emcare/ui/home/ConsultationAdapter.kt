package com.argusoft.who.emcare.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ListItemConsultationBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.toBinding

class ConsultationAdapter(
    val list: ArrayList<ConsultationItemData?> = arrayListOf() ,
    private val onClickListener: View.OnClickListener
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

        init {
            itemView.setOnClickListener {
                it.navigate(R.id.action_homeFragment_to_patientQuestionnaireFragment){
                    putString(INTENT_EXTRA_QUESTIONNAIRE_NAME, list[bindingAdapterPosition]?.questionnaireName)
                    putString(INTENT_EXTRA_STRUCTUREMAP_NAME, list[bindingAdapterPosition]?.questionnaireName)
                    putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, list[bindingAdapterPosition]?.header)
                    putString(INTENT_EXTRA_PATIENT_ID,list[bindingAdapterPosition]?.patientId)
                    putString(INTENT_EXTRA_ENCOUNTER_ID,list[bindingAdapterPosition]?.encounterId) }
            }
        }

        fun bind(album: ConsultationItemData) = with(album) {
            binding.nameTextView.text = name
            binding.dateOfBirthValueTextView.text = dateOfBirth
            binding.consultationDateValueTextView.text = dateOfConsultation
            binding.badgeTextView.text = badgeText
            binding.rightConsultationImageView.setImageResource(consultationIcon!!)
        }
    }
}