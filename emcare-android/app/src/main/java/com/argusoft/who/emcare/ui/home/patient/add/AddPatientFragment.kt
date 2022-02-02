package com.argusoft.who.emcare.ui.home.patient.add

import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.parser.IParser
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentAddPatientBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_LOCATION_ID
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.patient.PatientViewModel
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.widget.CustomQuestionnaireFragment
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint
import org.hl7.fhir.r4.model.Questionnaire

@AndroidEntryPoint
class AddPatientFragment : BaseFragment<FragmentAddPatientBinding>() {

    private val patientViewModel: PatientViewModel by viewModels()
    private val questionnaireFragment = QuestionnaireFragment()

    override fun initView() {
        setupToolbar()
        patientViewModel.getQuestionnaire("EmCareA") //TODO: replace hardcoded questionnaire id.
    }

    private fun setupToolbar() {
        binding.headerLayout.toolbar.setTitleAndBack(R.string.title_add_patient)
        binding.headerLayout.toolbar.inflateMenu(R.menu.menu_save)
        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            patientViewModel.questionnaireJson?.let {
                patientViewModel.savePatient(
                    questionnaireFragment.getQuestionnaireResponse(), it,
                    requireArguments().getInt(INTENT_EXTRA_LOCATION_ID)
                )
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun addQuestionnaireFragment(questionnaire: Questionnaire) {
        val fhirCtx: FhirContext = FhirContext.forR4()
        val parser: IParser = fhirCtx.newJsonParser().setPrettyPrint(false)
        patientViewModel.questionnaireJson = parser.encodeResourceToString(questionnaire)
        patientViewModel.questionnaireJson?.let {
            questionnaireFragment.arguments = bundleOf(QuestionnaireFragment.EXTRA_QUESTIONNAIRE_JSON_STRING to it)
            childFragmentManager.commit {
                add(R.id.fragmentContainerView, questionnaireFragment, QuestionnaireFragment::class.java.simpleName)
            }
        }
    }

    override fun initListener() {

    }

    override fun initObserver() {
        observeNotNull(patientViewModel.addPatients) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout)) {
                if (it == 1) {
                    requireActivity().onBackPressed()
                }
            }
        }
        observeNotNull(patientViewModel.questionnaire) { questionnaire ->
            addQuestionnaireFragment(questionnaire)
        }
    }
}