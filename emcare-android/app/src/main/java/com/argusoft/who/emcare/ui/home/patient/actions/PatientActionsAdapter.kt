package com.argusoft.who.emcare.ui.home.patient.actions

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.PatientActionsItemBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.PatientQuestionnaireData
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.toBinding

class PatientActionsAdapter(
    val patientId: String?,
    val list: ArrayList<PatientQuestionnaireData?> = arrayListOf(),
) : BaseAdapter<PatientQuestionnaireData>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is PatientActionsAdapter.ViewHolder -> {
                list[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: PatientActionsItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                it.navigate(R.id.action_patientActionsFragment_to_patientQuestionnaireFragment){
                    putString(INTENT_EXTRA_QUESTIONNAIRE_NAME, list[bindingAdapterPosition]?.questionnaireName)
                    putString(INTENT_EXTRA_STRUCTUREMAP_NAME, list[bindingAdapterPosition]?.questionnaireName)
                    putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, list[bindingAdapterPosition]?.header)
                    putString(INTENT_EXTRA_PATIENT_ID,patientId)
                }
            }
        }
        fun bind(item: PatientQuestionnaireData) = with(item) {
            binding.headerTextView.text = header
            binding.iconImageView.setImageResource(icon)
        }
    }

}