package com.argusoft.who.emcare.ui.home.patient.profile

import android.view.View
import androidx.activity.addCallback
import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPreviousConsultationQuestionnaireBinding
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat

@AndroidEntryPoint
class PreviousConsultationQuestionnaireFragment: BaseFragment<FragmentPreviousConsultationQuestionnaireBinding>() {

    private var questionnaireFragment = QuestionnaireFragment()
    private val previousConsultationQuestionnaireViewModel: PreviousConsultationQuestionnaireViewModel by viewModels()

    override fun initView() {
        (activity as? HomeActivity)?.closeSidepane()

        binding.headerLayout.toolbar.setTitleSidepane(
            requireArguments().getString(
                INTENT_EXTRA_QUESTIONNAIRE_HEADER
            )
        )

        requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_ID)?.let {
            previousConsultationQuestionnaireViewModel.getClosedQuestionnaire(it)
        }

        requireActivity().onBackPressedDispatcher.addCallback(this) {
            navigate(R.id.action_previousConsultationQuestionnaireFragment_to_patientProfileFragment) {
                    putString(INTENT_EXTRA_PATIENT_ID, requireArguments().getString(INTENT_EXTRA_PATIENT_ID))
            }

        }

        previousConsultationQuestionnaireViewModel.getPatient(requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!)
        previousConsultationQuestionnaireViewModel.getSidePaneItems(
            requireArguments().getString(INTENT_EXTRA_ENCOUNTER_ID)!!,
            requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!
        )
    }

    override fun initListener() {
        // No listener required
    }

    private fun addQuestionnaireFragment(pair: Pair<String, String>) {
        val cleanedQuestionnairePair = previousConsultationQuestionnaireViewModel.cleanQuestionnairePair(pair)
        previousConsultationQuestionnaireViewModel.questionnaireJson = cleanedQuestionnairePair.first
        previousConsultationQuestionnaireViewModel.questionnaireJson?.let {
            questionnaireFragment = QuestionnaireFragment.builder()
                .setQuestionnaire(cleanedQuestionnairePair.first)
                .setQuestionnaireResponse(cleanedQuestionnairePair.second)
                .setIsReadOnly(true)
                .build()
            childFragmentManager.commit {
                add(
                    R.id.fragmentContainerView,
                    questionnaireFragment,
                    QuestionnaireFragment::class.java.simpleName
                )
            }
        }
    }
    override fun initObserver() {
        observeNotNull(previousConsultationQuestionnaireViewModel.patient) { apiResponse ->
            apiResponse.whenSuccess { patientItem ->
                binding.nameTextView.text = patientItem.nameFirstRep.nameAsSingleString.orEmpty {
                    patientItem.identifierFirstRep.value ?: "NA #${patientItem.id?.takeLast(9)}"
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

        observeNotNull(previousConsultationQuestionnaireViewModel.sidepaneItems) { apiResponse ->
            apiResponse.whenSuccess {
                (activity as? HomeActivity)?.setupSidepane(true)
                (activity as? HomeActivity)?.sidepaneAdapter?.clearAllItems()
                (activity as? HomeActivity)?.sidepaneAdapter?.addAll(it)
            }
        }

        observeNotNull(previousConsultationQuestionnaireViewModel.questionnaire) { questionnaire ->
            questionnaire.handleApiView(
                binding.progressLayout,
                skipIds = listOf(R.id.headerLayout)
            ) {
                it?.let {
                    addQuestionnaireFragment(
                        it to requireArguments().getString(INTENT_EXTRA_QUESTIONNAIRE_RESPONSE)!!
                    )
                }
            }
        }
    }
}