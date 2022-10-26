package com.argusoft.who.emcare.ui.home.patient.add

import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentAddPatientBinding
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_REGISTRATION_PATIENT
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_FACILITY_ID
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.common.stageToQuestionnaireId
import com.argusoft.who.emcare.ui.common.stageToStructureMapId
import com.argusoft.who.emcare.ui.home.HomeViewModel
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
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
    private val questionnaireFragment = QuestionnaireFragment()

    override fun initView() {
        binding.headerLayout.toolbar.title = getString(R.string.title_emcare_registration)
        homeViewModel.getQuestionnaireWithQR(stageToQuestionnaireId[CONSULTATION_STAGE_REGISTRATION_PATIENT]!!, UUID.randomUUID().toString(), UUID.randomUUID().toString())
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
            questionnaireFragment.arguments =
                bundleOf(
                    QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to pair.first,
                    QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING to pair.second,
                    QuestionnaireFragment.EXTRA_ENABLE_REVIEW_PAGE to true
                )
            childFragmentManager.commit {
                add(R.id.fragmentContainerView, questionnaireFragment, QuestionnaireFragment::class.java.simpleName)
            }
        }
    }

    override fun initListener() {
    }

    override fun initObserver() {
        observeNotNull(homeViewModel.saveQuestionnaire) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                if (it is ConsultationFlowItem) {
                    navigate(R.id.action_addPatientFragment_to_homeFragment)
                }
            }
        }

        observeNotNull(homeViewModel.questionnaireWithQR) { questionnaire ->
            questionnaire.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                it?.let { addQuestionnaireFragmentWithQR(it) }
            }
        }
//        observeNotNull(settingsViewModel.languageApiState) {
//            it.whenSuccess {
//                it.languageData?.convertToMap()?.apply {
//                    binding.headerLayout.toolbar.setTitleSidepane(getOrElse("Add_Patient") { getString(R.string.title_add_patient) } )
//                }
//            }
//        }
    }
}