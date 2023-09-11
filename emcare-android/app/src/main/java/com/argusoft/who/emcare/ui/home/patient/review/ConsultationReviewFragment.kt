package com.argusoft.who.emcare.ui.home.patient.review

import android.view.View
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentConsultationReviewFragmentBinding
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_ASSESSMENTS
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_CLASSIFICATIONS
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_DANGER_SIGNS
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_MEASUREMENTS
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_REGISTRATION_ENCOUNTER
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_SIGNS
import com.argusoft.who.emcare.ui.common.CONSULTATION_STAGE_SYMPTOMS
import com.argusoft.who.emcare.ui.common.DATE_FORMAT_2
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_ENCOUNTER_ID
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_ID
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_QUESTIONNAIRE_HEADER
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.ConsultationFlowItem
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.ui.home.HomeViewModel
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class ConsultationReviewFragment: BaseFragment<FragmentConsultationReviewFragmentBinding>() {

    private val consultationReviewViewModel: ConsultationReviewViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()
    private var lastQuestionnaireFragment = QuestionnaireFragment()

    override fun initView() {
        (activity as? HomeActivity)?.closeSidepane() //Close the sidepane if open
        binding.headerLayout.toolbar.setTitleSidepane(getString(R.string.review)) //Setting up toolbar with sidepane
        consultationReviewViewModel.getConsultationsWithQuestionnaire(requireArguments().getString(INTENT_EXTRA_ENCOUNTER_ID)!!) //Fetching questionnaires with consultations

        childFragmentManager.setFragmentResultListener(
            QuestionnaireFragment.SUBMIT_REQUEST_KEY,
            viewLifecycleOwner
        ) { _, _ ->
            //TODO: navigate to next and save classification consultation

        }

        homeViewModel.getPatient(requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!)
        homeViewModel.getSidePaneItems(
            requireArguments().getString(INTENT_EXTRA_ENCOUNTER_ID)!!,
            requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!
        )
    }

    override fun initListener() {

    }

    private fun addQuestionnaireFragmentWithQRReview(questionnaire: String, consultationFlowItem: ConsultationFlowItem) {

        val questionnaireFragment = QuestionnaireFragment.builder()
            .setQuestionnaire(questionnaire)
            .setQuestionnaireResponse(consultationFlowItem.questionnaireResponseText!!)
            .setIsReadOnly(true)
            .showReviewPageBeforeSubmit(false)
            .setShowSubmitButton(false)
            .setCustomQuestionnaireItemViewHolderFactoryMatchersProvider("CUSTOM")
            .build()

        lastQuestionnaireFragment = QuestionnaireFragment.builder()
            .setQuestionnaire(questionnaire)
            .setQuestionnaireResponse(consultationFlowItem.questionnaireResponseText!!)
            .setIsReadOnly(true)
            .showReviewPageBeforeSubmit(false)
            .setShowSubmitButton(true)
            .setCustomQuestionnaireItemViewHolderFactoryMatchersProvider("CUSTOM")
            .build()
        when(consultationFlowItem.consultationStage){
            CONSULTATION_STAGE_REGISTRATION_ENCOUNTER -> {
                binding.registrationStageTextView.visibility = View.VISIBLE
                binding.registrationFragmentContainerView.visibility = View.VISIBLE
                childFragmentManager.commit {
                    add(R.id.registrationFragmentContainerView, questionnaireFragment, "Registration" + QuestionnaireFragment::class.java.simpleName)
                }
            }
            CONSULTATION_STAGE_DANGER_SIGNS -> {
                binding.dangerSignsStageTextView.visibility = View.VISIBLE
                binding.dangerSignsFragmentContainerView.visibility = View.VISIBLE
                childFragmentManager.commit {
                    add(R.id.dangerSignsFragmentContainerView, questionnaireFragment, "DangerSigns" + QuestionnaireFragment::class.java.simpleName)
                }
            }
            CONSULTATION_STAGE_MEASUREMENTS -> {
                binding.measurementsStageTextView.visibility = View.VISIBLE
                binding.measurementsFragmentContainerView.visibility = View.VISIBLE
                childFragmentManager.commit {
                    add(R.id.measurementsFragmentContainerView, questionnaireFragment, "Measurements" + QuestionnaireFragment::class.java.simpleName)
                }
            }
            CONSULTATION_STAGE_SYMPTOMS -> {
                binding.symptomsStageTextView.visibility = View.VISIBLE
                binding.symptomsFragmentContainerView.visibility = View.VISIBLE
                childFragmentManager.commit {
                    add(R.id.symptomsFragmentContainerView, questionnaireFragment, "Symptoms" + QuestionnaireFragment::class.java.simpleName)
                }
            }
            CONSULTATION_STAGE_SIGNS -> {
                binding.signsStageTextView.visibility = View.VISIBLE
                binding.signsFragmentContainerView.visibility = View.VISIBLE
                childFragmentManager.commit {
                    add(R.id.signsFragmentContainerView, questionnaireFragment, "Signs" + QuestionnaireFragment::class.java.simpleName)
                }
            }
            CONSULTATION_STAGE_ASSESSMENTS -> {
                binding.assessmentsStageTextView.visibility = View.VISIBLE
                binding.assessmentsFragmentContainerView.visibility = View.VISIBLE
                childFragmentManager.commit {
                    add(R.id.assessmentsFragmentContainerView, questionnaireFragment, "Assessments" + QuestionnaireFragment::class.java.simpleName)
                }
            }
            CONSULTATION_STAGE_CLASSIFICATIONS -> {
                binding.classificationsStageTextView.visibility = View.VISIBLE
                binding.classificationsFragmentContainerView.visibility = View.VISIBLE
                childFragmentManager.commit {
                    add(R.id.classificationsFragmentContainerView, lastQuestionnaireFragment, "Classification" + QuestionnaireFragment::class.java.simpleName)
                }
            }
        }
        childFragmentManager.commit {
            add(R.id.fragmentContainerView, questionnaireFragment, QuestionnaireFragment::class.java.simpleName)
        }
    }

    override fun initObserver() {

        observeNotNull(homeViewModel.patient) { apiResponse ->
            apiResponse.whenSuccess { patientItem ->
                binding.nameTextView.text = patientItem.nameFirstRep.nameAsSingleString.orEmpty {
                    patientItem.identifierFirstRep.value ?: "#${patientItem.id?.take(9)}"
                }
                val dateOfBirth = patientItem.birthDateElement.valueAsString
                if(dateOfBirth != null && dateOfBirth.isNotBlank()){
                    val oldFormatDate = SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirth)
                    binding.dobTextView.text = SimpleDateFormat(DATE_FORMAT_2).format(oldFormatDate!!)
                } else {
                    binding.dobTextView.text = "Not Provided"
                }
                if (!patientItem.hasGender()) {
                    if (patientItem.genderElement.valueAsString.equals("male", false))
                        binding.childImageView.setImageResource(R.drawable.baby_boy)
                    else
                        binding.childImageView.setImageResource(R.drawable.baby_girl)
                }
                binding.childImageView.visibility = View.VISIBLE
                binding.dobTextViewLabel.visibility = View.VISIBLE
            }
        }

        observeNotNull(consultationReviewViewModel.questionnaireConsultationMap) {
            it.forEach { (questionnaire, consultationFlowItem) ->
                addQuestionnaireFragmentWithQRReview(questionnaire, consultationFlowItem)
            }
        }
    }

}