package com.argusoft.who.emcare.ui.home.location

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.databinding.ListItemLocationBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.Location
import com.argusoft.who.emcare.utils.extention.toBinding

class LocationAdapter(
    val list: ArrayList<Location?> = arrayListOf()
) : BaseAdapter<Location>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(parent.toBinding())
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        when (holder) {
            is LocationAdapter.ViewHolder -> {
                list[position]?.let { holder.bind(it) }
            }
        }
    }

    inner class ViewHolder(val binding: ListItemLocationBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Location) = with(item) {
            binding.locationEditText.hint =  type
            //TODO: add location select option items
        }
    }
}