package com.argusoft.who.emcare.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ListItemConsultationBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.utils.extention.getColor
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.toBinding
import java.text.SimpleDateFormat

class ConsultationAdapter(
    val list: ArrayList<ConsultationItemData?> = arrayListOf() ,
    private val onClickListener: View.OnClickListener,
    diffCallBack: DiffUtil.ItemCallback<ConsultationItemData>
) : PagingDataAdapter<ConsultationItemData, ConsultationAdapter.ViewHolder>(diffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsultationAdapter.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: ConsultationAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
        }
    }

    fun clearAllItems() {
        list.clear()
        list.trimToSize()
        notifyDataSetChanged()
    }

    fun addAll(dataList: List<ConsultationItemData>) {
        list.addAll(dataList)
        notifyItemChanged(
            list.lastIndex - list.lastIndex,
            list.lastIndex
        )
    }

    inner class ViewHolder(val binding: ListItemConsultationBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                it.navigate(R.id.action_homeFragment_to_patientQuestionnaireFragment){
                    putString(INTENT_EXTRA_QUESTIONNAIRE_ID, getItem(bindingAdapterPosition)?.questionnaireId)
                    putString(INTENT_EXTRA_STRUCTUREMAP_ID, getItem(bindingAdapterPosition)?.structureMapId)
                    putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, getItem(bindingAdapterPosition)?.header)
                    putString(INTENT_EXTRA_CONSULTATION_FLOW_ITEM_ID, getItem(bindingAdapterPosition)?.consultationFlowItemId)
                    putString(INTENT_EXTRA_PATIENT_ID,getItem(bindingAdapterPosition)?.patientId)
                    putString(INTENT_EXTRA_ENCOUNTER_ID,getItem(bindingAdapterPosition)?.encounterId)
                    putString(INTENT_EXTRA_CONSULTATION_STAGE,getItem(bindingAdapterPosition)?.consultationStage)
                    putString(INTENT_EXTRA_QUESTIONNAIRE_RESPONSE,getItem(bindingAdapterPosition)?.questionnaireResponseText)
                    getItem(bindingAdapterPosition)?.isActive?.let { it1 ->
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
                val oldFormatDate = SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirth)
                binding.dateOfBirthValueTextView.text = SimpleDateFormat(DATE_FORMAT).format(oldFormatDate!!)
            }
            binding.consultationDateValueTextView.text = dateOfConsultation
            binding.badgeTextView.text = badgeText
            binding.rightConsultationImageView.setImageResource(consultationIcon!!)
            binding.syncStateTextView.let {
                if (isSynced) {
                    binding.syncStateTextView.text = binding.root.context.getString(R.string.text_synced)
                    binding.syncStateTextView.background = ContextCompat.getDrawable(binding.root.context,
                        R.drawable.selector_bg_light_green)
                    binding.syncStateTextView.setTextColor(it.getColor(R.color.color_dark_green))
                    binding.syncStateTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_synced), null, null, null)
                } else {
                    binding.syncStateTextView.text = binding.root.context.getString(R.string.text_not_synced)
                    binding.syncStateTextView.background = ContextCompat.getDrawable(binding.root.context,
                        R.drawable.selector_bg_light_grey)
                    binding.syncStateTextView.setTextColor(it.getColor(R.color.color_dark_grey))
                    binding.syncStateTextView.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(binding.root.context,
                        R.drawable.ic_not_synced), null, null, null)
                }
            }
            if(!gender.isNullOrEmpty()){
                if(gender.equals("male" ,false))
                    binding.childImageView.setImageResource(R.drawable.baby_boy)
                else
                    binding.childImageView.setImageResource(R.drawable.baby_girl)
            }
        }
    }
}