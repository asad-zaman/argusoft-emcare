package com.argusoft.who.emcare.ui.home.patient

import android.content.res.ColorStateList
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.ListItemPatientBinding
import com.argusoft.who.emcare.ui.common.base.BaseAdapter
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.utils.extention.isNotEmpty
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.extention.toBinding
import org.hl7.fhir.r4.model.codesystems.RiskProbability

class PatientAdapter(
    val list: ArrayList<PatientItem?> = arrayListOf(),
    private val onClickListener: View.OnClickListener
) : BaseAdapter<PatientItem>(list) {

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

    inner class ViewHolder(val binding: ListItemPatientBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(album: PatientItem) = with(album) {
            binding.nameTextView.text = name.orEmpty { identifier ?:"" }
            binding.idTextView.text = binding.root.context.getString(R.string.label_id_with_colon, resourceId?.takeLast(3))
            binding.statusImageView.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(
                    binding.root.context,
                    when (risk) {
                        RiskProbability.HIGH.toCode() -> R.color.high_risk
                        RiskProbability.MODERATE.toCode() -> R.color.moderate_risk
                        RiskProbability.LOW.toCode() -> R.color.low_risk
                        else -> R.color.unknown_risk_background
                    }
                )
            )
        }
    }
}