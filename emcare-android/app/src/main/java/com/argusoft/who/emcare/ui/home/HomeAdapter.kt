package com.argusoft.who.emcare.ui.home

import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.databinding.ListItemDashboardBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.Dashboard
import com.argusoft.who.emcare.utils.extention.toBinding

class HomeAdapter(
    val list: ArrayList<Dashboard?> = arrayListOf(),
    private val onClickListener: View.OnClickListener
) : BaseAdapter<Dashboard>(list) {

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

    inner class ViewHolder(val binding: ListItemDashboardBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.itemRootLayout.setOnClickListener(onClickListener)
        }

        fun bind(album: Dashboard) = with(album) {
            binding.itemRootLayout.tag = bindingAdapterPosition
            binding.iconImageView.setImageResource(icon)
            binding.titleTextView.text = title
        }
    }
}