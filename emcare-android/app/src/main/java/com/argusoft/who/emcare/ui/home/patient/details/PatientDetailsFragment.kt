package com.argusoft.who.emcare.ui.home.patient.details

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientDetailsBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_ID
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_NAME
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PatientDetailsFragment : BaseFragment<FragmentPatientDetailsBinding>() {

    private val patientDetailsViewModel: PatientDetailsViewModel by viewModels()
    private lateinit var patientDetailsAdapter: PatientDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientDetailsAdapter = PatientDetailsAdapter()
    }

    override fun initView() {
        binding.headerLayout.toolbar.setTitleAndBack(R.string.title_patient_details)
        setupRecyclerView()
        patientDetailsViewModel.getPatientDetails(requireArguments().getString(INTENT_EXTRA_PATIENT_ID))
        binding.patientName.text = requireArguments().getString(INTENT_EXTRA_PATIENT_NAME)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = patientDetailsAdapter
    }

    override fun initListener() {
        binding.deletePatientButton.setOnClickListener(this)
    }

    override fun initObserver() {
        observeNotNull(patientDetailsViewModel.patientItem) { apiResponse ->
            apiResponse.handleApiView(binding.patientDetailsLayout) {
                patientDetailsAdapter.clearAllItems()
                patientDetailsAdapter.addAll(patientDetailsViewModel.createPatientItemDataListFromPatientItem(it))
            }
        }

        observeNotNull(patientDetailsViewModel.deletePatientLoadingState) {
            it.handleApiView(binding.patientDetailsLayout)
        }

        observeNotNull(patientDetailsViewModel.deletePatientSuccessState) {
            requireActivity().onBackPressed()
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.delete_patient_button -> {
                patientDetailsViewModel.deletePatient(requireArguments().getString(INTENT_EXTRA_PATIENT_ID))
            }
        }
    }
}