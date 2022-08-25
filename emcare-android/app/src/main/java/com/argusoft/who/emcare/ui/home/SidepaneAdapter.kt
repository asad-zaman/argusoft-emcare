package com.argusoft.who.emcare.ui.home

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.databinding.ListItemSidepaneBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.SidepaneItem
import com.argusoft.who.emcare.utils.extention.toBinding

class SidepaneAdapter(
    val list: ArrayList<SidepaneItem?> = arrayListOf(),
    private val onClickListener: View.OnClickListener
) : BaseAdapter<SidepaneItem>(list) {

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

    inner class ViewHolder(val binding: ListItemSidepaneBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            itemView.setOnClickListener {
                //Todo: Add a when statement with proper navigation results
//                it.navigate(R.id.action_homeFragment_to_patientActionsFragment) {
//                }
            }
        }
        fun bind(obj: SidepaneItem) = with(obj) {
            binding.iconImageView.setImageResource(iconId!!)
            binding.descriptionTextView.text = description
        }
    }
}