package com.argusoft.who.emcare.ui.home.patient.actions

import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientQuestionnaireBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeViewModel
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint
import org.hl7.fhir.r4.model.Questionnaire

@AndroidEntryPoint
class PatientQuestionnaireFragment : BaseFragment<FragmentPatientQuestionnaireBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val questionnaireFragment = QuestionnaireFragment()

    override fun initView() {
        binding.headerLayout.toolbar.setTitleSidepane(getString(R.string.patient) + " " + requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_HEADER))

        requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_NAME)?.let {
            homeViewModel.getQuestionnaireWithQR(it, requireArguments().getString(INTENT_EXTRA_PATIENT_ID),requireArguments().getString(INTENT_EXTRA_ENCOUNTER_ID)) }

        childFragmentManager.setFragmentResultListener(
            QuestionnaireFragment.SUBMIT_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            homeViewModel.questionnaireJson?.let {
                homeViewModel.saveQuestionnaire(
                    questionnaireResponse = questionnaireFragment.getQuestionnaireResponse(),
                    questionnaire = it,
                    facilityId = requireArguments().getString(INTENT_EXTRA_FACILITY_ID)!!,
                    patientId = requireArguments().getString(INTENT_EXTRA_PATIENT_ID),
                    encounterId = requireArguments().getString(INTENT_EXTRA_ENCOUNTER_ID),
                )
            }
        }
    }

    private fun addQuestionnaireFragment(pair: Pair<String, String>) {
        homeViewModel.questionnaireJson = pair.first
        homeViewModel.questionnaireJson?.let {
            questionnaireFragment.arguments =
                bundleOf(
                    QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to pair.first,
                    QuestionnaireFragment.EXTRA_QUESTIONNAIRE_RESPONSE_JSON_STRING to pair.second
                )
            childFragmentManager.commit {
                add(R.id.fragmentContainerView, questionnaireFragment, QuestionnaireFragment::class.java.simpleName)
            }
        }
    }

    override fun initListener() {

    }

    override fun initObserver() {
        //TODO: Add option on saving
        observeNotNull(homeViewModel.saveQuestionnaire) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                if (it == 1) {
                    requireActivity().onBackPressed()
                }
            }
        }

        observeNotNull(homeViewModel.questionnaireWithQR) { questionnaire ->
            questionnaire.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                it?.let { addQuestionnaireFragment(it) }
            }
        }
//        observeNotNull(settingsViewModel.languageApiState) {
//            it.whenSuccess {
//                it.languageData?.convertToMap()?.apply {
//                    binding.headerLayout.toolbar.setTitleSidepane(
//                        getOrElse("Patient") { getString(R.string.patient) } + " "
//                            + requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_HEADER)  )
//                }
//            }
//        }
    }
}