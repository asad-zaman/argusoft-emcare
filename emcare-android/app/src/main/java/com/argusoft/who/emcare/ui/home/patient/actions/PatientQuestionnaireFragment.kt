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
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.convertToMap
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint
import org.hl7.fhir.r4.model.Questionnaire

@AndroidEntryPoint
class PatientQuestionnaireFragment : BaseFragment<FragmentPatientQuestionnaireBinding>() {

    private val patientActionsViewModel: PatientActionsViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val questionnaireFragment = QuestionnaireFragment()
    private var patientId:String? = ""

    override fun initView() {
        binding.headerLayout.toolbar.setTitleSidepane(getString(R.string.patient)
                    + " " + requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_HEADER)  )
        requireArguments().getString(
            INTENT_EXTRA_QUESTIONNAIRE_NAME)?.let { patientActionsViewModel.getQuestionnaire(it) }
        patientId = requireArguments().getString(INTENT_EXTRA_PATIENT_ID)
    }

    private fun addQuestionnaireFragment(questionnaire: Questionnaire) {
        val fhirCtx: FhirContext = FhirContext.forR4()
        val parser: IParser = fhirCtx.newJsonParser().setPrettyPrint(false)
        patientActionsViewModel.questionnaireJson = parser.encodeResourceToString(questionnaire)
        patientActionsViewModel.questionnaireJson?.let {
            questionnaireFragment.arguments = bundleOf(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to it)
            childFragmentManager.commit {
                add(R.id.fragmentContainerView, questionnaireFragment, QuestionnaireFragment::class.java.simpleName)
            }
        }
    }

    override fun initListener() {

    }

    override fun initObserver() {
        //TODO: Add option on saving
        observeNotNull(patientActionsViewModel.saveQuestionnaire) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                if (it == 1) {
                    requireActivity().onBackPressed()
                }
            }
        }
        observeNotNull(patientActionsViewModel.questionnaire) { questionnaire ->
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