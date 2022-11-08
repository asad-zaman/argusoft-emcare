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
import java.text.SimpleDateFormat

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
                        putBoolean(INTENT_EXTRA_IS_ACTIVE,
                            it1
                        )
                    }
                }
            }
        }

        fun bind(album: ConsultationItemData) = with(album) {
            binding.nameTextView.setText(name)
            //Using correct date format
            if(dateOfBirth != null && !dateOfBirth.equals("Not Provided", true) && dateOfBirth.isNotBlank()){
                val oldFormatDate = SimpleDateFormat("YYYY-MM-DD").parse(dateOfBirth)
                binding.dateOfBirthValueTextView.text = SimpleDateFormat(DATE_FORMAT).format(oldFormatDate!!)
            }
            binding.consultationDateValueTextView.text = dateOfConsultation
            binding.badgeTextView.text = badgeText
            binding.rightConsultationImageView.setImageResource(consultationIcon!!)
            if(!gender.isNullOrEmpty()){
                if(gender.equals("male" ,false))
                    binding.childImageView.setImageResource(R.drawable.baby_boy)
                else
                    binding.childImageView.setImageResource(R.drawable.baby_girl)
            }
        }
    }
}