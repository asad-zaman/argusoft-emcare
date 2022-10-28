package com.argusoft.who.emcare.ui.home.patient.profile

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ListItemActiveConsultationBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.ActiveConsultationData
import com.argusoft.who.emcare.utils.extention.navigate
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

        init {
            itemView.setOnClickListener {
                it.navigate(R.id.action_patientProfileFragment_to_patientQuestionnaireFragment){
                    putString(INTENT_EXTRA_QUESTIONNAIRE_ID, list[bindingAdapterPosition]?.questionnaireId)
                    putString(INTENT_EXTRA_STRUCTUREMAP_ID, list[bindingAdapterPosition]?.structureMapId)
//                    putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, list[bindingAdapterPosition]?.badgeText)
                    putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, list[bindingAdapterPosition]?.header) //For testing only replace it with badgeText
                    putString(INTENT_EXTRA_CONSULTATION_FLOW_ITEM_ID, list[bindingAdapterPosition]?.consultationFlowItemId)
                    putString(INTENT_EXTRA_PATIENT_ID,list[bindingAdapterPosition]?.patientId)
                    putString(INTENT_EXTRA_ENCOUNTER_ID,list[bindingAdapterPosition]?.encounterId)
                    putString(INTENT_EXTRA_CONSULTATION_STAGE,list[bindingAdapterPosition]?.consultationStage)
                    putString(INTENT_EXTRA_QUESTIONNAIRE_RESPONSE,list[bindingAdapterPosition]?.questionnaireResponseText)
                    list[bindingAdapterPosition]?.isActive?.let { it1 ->
                        putBoolean(
                            INTENT_EXTRA_IS_ACTIVE,
                            it1
                        )
                    }
                }
            }
        }

        fun bind(item: ActiveConsultationData) = with(item) {
            binding.consultationNameTextView.text = consultationLabel
            binding.dateTextView.text = dateOfConsultation
            binding.statusImageView.setImageResource(consultationIcon!!)
        }
    }

}