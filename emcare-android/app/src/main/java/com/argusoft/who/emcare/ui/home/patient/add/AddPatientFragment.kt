package com.argusoft.who.emcare.ui.home.patient.add

import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentAddPatientBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.home.HomeViewModel
import com.argusoft.who.emcare.utils.extention.alertDialog
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.SUBMIT_REQUEST_KEY
import java.util.*


@AndroidEntryPoint
class AddPatientFragment : BaseFragment<FragmentAddPatientBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()
//    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private var questionnaireFragment = QuestionnaireFragment()

    override fun initView() {
        binding.headerLayout.toolbar.title = getString(R.string.title_emcare_registration)
        homeViewModel.getQuestionnaireWithQR(stageToQuestionnaireId[CONSULTATION_STAGE_REGISTRATION_PATIENT]!!, UUID.randomUUID().toString(), UUID.randomUUID().toString(), isPreviouslySavedConsultation = false)
        childFragmentManager.setFragmentResultListener(SUBMIT_REQUEST_KEY, viewLifecycleOwner) { _, _ ->
            homeViewModel.questionnaireJson?.let {
                homeViewModel.saveQuestionnaire(
                    questionnaireResponse = questionnaireFragment.getQuestionnaireResponse(),
                    questionnaire = it,
                    facilityId = preference.getLoggedInUser()?.facility?.get(0)?.facilityId!!,
                    structureMapId = stageToStructureMapId[CONSULTATION_STAGE_REGISTRATION_PATIENT]!!,
                    consultationFlowItemId = null,
                    consultationStage = CONSULTATION_STAGE_REGISTRATION_PATIENT
                )
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            activity?.alertDialog {
                setMessage(R.string.msg_exit_registration)
                setPositiveButton(R.string.button_yes) { _, _ ->
                    navigate(R.id.action_addPatientFragment_to_homeFragment)
                }
                setNegativeButton(R.string.button_no) { _, _ -> }
            }?.show()
        }
    }


    private fun addQuestionnaireFragmentWithQR(pair: Pair<String, String>) {
        homeViewModel.questionnaireJson = pair.first
        homeViewModel.questionnaireJson?.let {
            questionnaireFragment = QuestionnaireFragment.builder()
                .setQuestionnaire(pair.first)
                .setQuestionnaireResponse(pair.second)
                .showReviewPageBeforeSubmit(true)
                .build()
            childFragmentManager.commit {
                add(R.id.fragmentContainerView, questionnaireFragment, QuestionnaireFragment::class.java.simpleName)
            }
        }
    }

    override fun initListener() {
        binding.resetQuestionnaireButton.setOnClickListener(this)
    }

    override fun initObserver() {
        observeNotNull(homeViewModel.saveQuestionnaire) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                if (it is ConsultationFlowItem) {
                    findNavController().popBackStack(R.id.addPatientFragment, true)
                    navigate(R.id.action_homeFragment_to_patientQuestionnaireFragment) {
                        putString(INTENT_EXTRA_QUESTIONNAIRE_ID, it.questionnaireId)
                        putString(INTENT_EXTRA_STRUCTUREMAP_ID, it.structureMapId)
                        putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, it.questionnaireId)
                        putString(INTENT_EXTRA_CONSULTATION_FLOW_ITEM_ID, it.id)
                        putString(INTENT_EXTRA_PATIENT_ID, it.patientId)
                        putString(INTENT_EXTRA_ENCOUNTER_ID, it.encounterId)
                        putString(INTENT_EXTRA_CONSULTATION_STAGE, it.consultationStage)
                        putString(INTENT_EXTRA_QUESTIONNAIRE_RESPONSE, it.questionnaireResponseText)
                    }
                } else {
                    navigate(R.id.action_addPatientFragment_to_homeFragment)
                }
            }
        }

        observeNotNull(homeViewModel.questionnaireWithQR) { questionnaire ->
            questionnaire.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                it?.let { addQuestionnaireFragmentWithQR(it) }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.reset_questionnaire_button -> {
                activity?.alertDialog {
                    setMessage(R.string.msg_reset_questionnaire)
                    setPositiveButton(R.string.button_yes) { _, _ ->
                        navigate(R.id.action_addPatientFragment_to_addPatientFragment) {
                            putString(INTENT_EXTRA_FACILITY_ID, preference.getLoggedInUser()?.facility?.get(0)?.facilityId)
                        }
                    }
                    setNegativeButton(R.string.button_no) { _, _ -> }
                }?.show()
            }
        }
    }
}