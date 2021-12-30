package com.argusoft.who.emcare.ui.home.location

import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentLocationBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_LOCATION_ID
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.Location
import com.argusoft.who.emcare.utils.extention.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationFragment : BaseFragment<FragmentLocationBinding>() {

    private val locationViewModel: LocationViewModel by viewModels()

    override fun initView() {
        binding.headerLayout.toolbar.setTitleAndBack(R.string.title_registration)
    }

    override fun initListener() {
        binding.selectButton.setOnClickListener(this)
        binding.locationEditText.onTextChanged { locationViewModel.locationId = null }
        binding.locationEditText.setOnItemClickListener { parent, view, position, id ->
            locationViewModel.locationId = (binding.locationTextInputLayout.tag as? List<Location>)?.getOrNull(position)?.id
        }
    }

    private fun setupLocationAutoComplete(locationList: List<Location>) {
        binding.locationTextInputLayout.tag = locationList
        binding.locationEditText.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.select_dialog_item,
                locationList.map { it.name }
            )
        )
    }

    override fun initObserver() {
        observeNotNull(locationViewModel.locationAndRolesApiState) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout) {
                it?.let { list -> setupLocationAutoComplete(list) }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.selectButton -> {
                if (locationViewModel.locationId == null) {
                    context?.showSnackBar(
                        view = binding.progressLayout,
                        message = getString(R.string.error_msg_select_location),
                        isError = true
                    )
                } else {
                    navigate(R.id.action_locationFragment_to_patientFragment) {
                        putInt(INTENT_EXTRA_LOCATION_ID, locationViewModel.locationId!!)
                    }
                }
            }
        }
    }
}