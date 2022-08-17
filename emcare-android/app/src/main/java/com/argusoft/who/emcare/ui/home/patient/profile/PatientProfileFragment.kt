package com.argusoft.who.emcare.ui.home.patient.profile

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientProfileBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_DOB
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_NAME
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.convertToMap
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.whenSuccess
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PatientProfileFragment : BaseFragment<FragmentPatientProfileBinding>() {

    private val patientProfileViewModel: PatientProfileViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private lateinit var activeConsultationsAdapter: PatientProfileActiveConsultationsAdapter
    private lateinit var previousConsultationsAdapter: PatientProfilePreviousConsultationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activeConsultationsAdapter = PatientProfileActiveConsultationsAdapter()
        previousConsultationsAdapter = PatientProfilePreviousConsultationsAdapter()
    }

    override fun initView() {
        setupActiveConsultationsRecyclerView()
        setupPreviousConsultationsRecyclerView()
        binding.nameTextView.text = requireArguments().getString(INTENT_EXTRA_PATIENT_NAME)
        binding.dobTextView.text = requireArguments().getString(INTENT_EXTRA_PATIENT_DOB)
    }

    private fun setupActiveConsultationsRecyclerView() {
        binding.activeConsultationRecyclerView.adapter = activeConsultationsAdapter
        activeConsultationsAdapter.clearAllItems()
        activeConsultationsAdapter.addAll(patientProfileViewModel.getActiveConsultations())

    }

    private fun setupPreviousConsultationsRecyclerView() {
        binding.previousConsultationRecyclerView.adapter = previousConsultationsAdapter
        previousConsultationsAdapter.clearAllItems()
        previousConsultationsAdapter.addAll(patientProfileViewModel.getPreviousConsultations())
    }

    override fun initListener() {
    }

    override fun initObserver() {
        observeNotNull(settingsViewModel.languageApiState) {
            it.whenSuccess {
                it.languageData?.convertToMap()?.apply {
                    binding.headerLayout.toolbar.setTitleDashboardSidepane(id = getOrElse("Patient_Actions") { getString(R.string.title_patient_profile) })
                }
            }
        }
    }

}