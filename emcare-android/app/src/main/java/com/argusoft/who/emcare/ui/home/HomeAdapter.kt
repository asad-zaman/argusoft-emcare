package com.argusoft.who.emcare.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ListItemPatientBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_DOB
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_ID
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_NAME
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.utils.extention.getColor
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.extention.toBinding

class HomeAdapter(
    val list: ArrayList<PatientItem?> = arrayListOf(),
    private val onClickListener: View.OnClickListener,
    diffCallBack: DiffUtil.ItemCallback<PatientItem>
) : PagingDataAdapter<PatientItem, HomeAdapter.ViewHolder>(diffCallBack) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomeAdapter.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: HomeAdapter.ViewHolder, position: Int) {
        getItem(position)?.let { item ->
            holder.bind(item)
        }
    }

    inner class ViewHolder(val binding: ListItemPatientBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                it.navigate(R.id.action_homeFragment_to_patientProfileFragment) {
                    putString(INTENT_EXTRA_PATIENT_ID,getItem(bindingAdapterPosition)?.resourceId)
                    putString(INTENT_EXTRA_PATIENT_NAME,if(getItem(bindingAdapterPosition)?.name.isNullOrEmpty()) (if (getItem(bindingAdapterPosition)?.identifier.isNullOrEmpty()) getItem(bindingAdapterPosition)?.resourceId else getItem(bindingAdapterPosition)?.identifier) else getItem(bindingAdapterPosition)?.name)
                    putString(INTENT_EXTRA_PATIENT_DOB,getItem(bindingAdapterPosition)?.dob)
                }
            }
        }
        fun bind(album: PatientItem) = with(album) {
            var patientName = name.orEmpty { identifier ?:"#${resourceId?.take(8)}"}
            if(patientName.isEmpty()) {
                patientName = "#${resourceId?.take(8)}"
            }
            binding.nameTextView.setText(patientName)
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
            binding.idTextView.text = binding.root.context.getString(R.string.label_id_with_colon, resourceId?.takeLast(3))
            if(!gender.isNullOrEmpty()){
                if(gender.equals("male" ,false))
                    binding.statusImageView.setImageResource(R.drawable.baby_boy)
                else
                    binding.statusImageView.setImageResource(R.drawable.baby_girl)
            }
        }
    }
}