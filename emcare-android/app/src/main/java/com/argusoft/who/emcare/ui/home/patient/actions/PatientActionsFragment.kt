package com.argusoft.who.emcare.ui.home.patient.actions

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientActionsBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_ID
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_NAME
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.PatientQuestionnaireData
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.convertToMap
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.whenSuccess
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class PatientActionsFragment : BaseFragment<FragmentPatientActionsBinding>() {

    private val patientActionsViewModel: PatientActionsViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private lateinit var patientActionsAdapter: PatientActionsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientActionsAdapter = PatientActionsAdapter(requireArguments().getString(INTENT_EXTRA_PATIENT_ID))
    }

    override fun initView() {
        setupRecyclerView()
        patientActionsViewModel.getPatientDetails(requireArguments().getString(INTENT_EXTRA_PATIENT_ID))
        binding.patientName.text = requireArguments().getString(INTENT_EXTRA_PATIENT_NAME)
    }

    private fun setupRecyclerView() {
        binding.recyclerView.adapter = patientActionsAdapter
    }

    override fun initListener() {
    }

    override fun initObserver() {
        observeNotNull(patientActionsViewModel.patientItem) { apiResponse ->
            apiResponse.handleApiView(binding.patientActionsLayout) {
                patientActionsAdapter.clearAllItems()
                val patientActionsList = mutableListOf<PatientQuestionnaireData>()

                patientActionsList.add(PatientQuestionnaireData("Signs", "EmCare.B10-16.Signs.2m.p",R.drawable.ic_risk_assessment))
                patientActionsList.add(PatientQuestionnaireData("Symptoms", "emcare.b10-14.symptoms.2m.p",R.drawable.ic_dashboard_notification))
                patientActionsList.add(PatientQuestionnaireData("Measurements", "EmCare.B6.Measurements",R.drawable.ic_reports))
                patientActionsList.add(PatientQuestionnaireData("Danger Signs", "EmCare.B7.LTI-DangerSigns",R.drawable.ic_announcements))

                if(patientActionsAdapter.getItemsList().isEmpty()){
                    patientActionsAdapter.addAll(patientActionsList)
                }
            }
        }

        observeNotNull(patientActionsViewModel.deletePatientLoadingState) {
            it.handleApiView(binding.patientActionsLayout)
        }

        observeNotNull(patientActionsViewModel.deletePatientSuccessState) {
            requireActivity().onBackPressed()
        }

        observeNotNull(settingsViewModel.languageApiState) {
            it.whenSuccess {
                it.languageData?.convertToMap()?.apply {
                    binding.headerLayout.toolbar.setTitleAndBack(getOrElse("Patient_Actions") { getString(R.string.title_patient_actions) })
                }
            }
        }
    }

}