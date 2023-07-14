package com.argusoft.who.emcare.ui.home.patient.profile

import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ListItemPreviousConsultationBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.PreviousConsultationData
import com.argusoft.who.emcare.utils.extention.getColor
import com.argusoft.who.emcare.utils.extention.navigate
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

        init {
            itemView.setOnClickListener {
                it.navigate(R.id.action_patientProfileFragment_to_previousConsultationQuestionnaireFragment){
                    putString(INTENT_EXTRA_QUESTIONNAIRE_ID, list[bindingAdapterPosition]?.questionnaireId)
                    putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, list[bindingAdapterPosition]?.header)
                    putString(INTENT_EXTRA_PATIENT_ID,list[bindingAdapterPosition]?.patientId)
                    putString(INTENT_EXTRA_ENCOUNTER_ID,list[bindingAdapterPosition]?.encounterId)
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

        fun bind(item: PreviousConsultationData) = with(item) {
            binding.consultationNameTextView.text = consultationLabel
            binding.dateTextView.text = dateOfConsultation
            binding.syncStateTextView.let {
                if (isSynced) {
                    binding.syncStateTextView.text = binding.root.context.getString(R.string.text_synced)
                    binding.syncStateTextView.background = ContextCompat.getDrawable(binding.root.context,
                        R.drawable.selector_bg_light_green)
                    binding.syncStateTextView.setTextColor(it.getColor(R.color.color_dark_green))
                    binding.syncStateTextView.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(binding.root.context,
                            R.drawable.ic_synced), null, null, null)
                } else {
                    binding.syncStateTextView.text = binding.root.context.getString(R.string.text_not_synced)
                    binding.syncStateTextView.background = ContextCompat.getDrawable(binding.root.context,
                        R.drawable.selector_bg_light_grey)
                    binding.syncStateTextView.setTextColor(it.getColor(R.color.color_dark_grey))
                    binding.syncStateTextView.setCompoundDrawablesWithIntrinsicBounds(
                        ContextCompat.getDrawable(binding.root.context,
                            R.drawable.ic_not_synced), null, null, null)
                }
            }
            binding.consultationImageView.setImageResource(consultationIcon!!)
        }
    }

}