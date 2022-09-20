package com.argusoft.who.emcare.ui.home.patient.add

import android.view.MenuItem
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentAddPatientBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_FACILITY_ID
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_LOCATION_ID
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeViewModel
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.convertToMap
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint
import org.hl7.fhir.r4.model.Questionnaire
import com.google.android.fhir.datacapture.QuestionnaireFragment.Companion.SUBMIT_REQUEST_KEY


@AndroidEntryPoint
class AddPatientFragment : BaseFragment<FragmentAddPatientBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val questionnaireFragment = QuestionnaireFragment()

    override fun initView() {
        binding.headerLayout.toolbar.setTitleSidepane(getString(R.string.title_emcare_registration))
//        homeViewModel.getQuestionnaire("emcarea.registration.p") //TODO: replace hardcoded questionnaire id.
        homeViewModel.getQuestionnaireWithQR("emcarea.registration.p")
        childFragmentManager.setFragmentResultListener(SUBMIT_REQUEST_KEY, viewLifecycleOwner) { _, _ ->
            homeViewModel.questionnaireJson?.let {
                homeViewModel.savePatient(
                    questionnaireFragment.getQuestionnaireResponse(), it,
                    requireArguments().getString(INTENT_EXTRA_FACILITY_ID)!!
                )
            }

        }
    }


    private fun addQuestionnaireFragment(questionnaire: Questionnaire) {
        val fhirCtx: FhirContext = FhirContext.forR4()
        val parser: IParser = fhirCtx.newJsonParser().setPrettyPrint(false)
        homeViewModel.questionnaireJson = parser.encodeResourceToString(questionnaire)
        homeViewModel.questionnaireJson?.let {
            questionnaireFragment.arguments = bundleOf(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to it)
            childFragmentManager.commit {
                add(R.id.fragmentContainerView, questionnaireFragment, QuestionnaireFragment::class.java.simpleName)
            }
        }
    }

    private fun addQuestionnaireFragmentWithQR(pair: Pair<String, String>) {
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
        observeNotNull(homeViewModel.addPatients) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                if (it == 1) {
                    requireActivity().onBackPressed()
                }
            }
        }
        observeNotNull(homeViewModel.questionnaire) { questionnaire ->
            questionnaire.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                it?.let { addQuestionnaireFragment(it) }
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