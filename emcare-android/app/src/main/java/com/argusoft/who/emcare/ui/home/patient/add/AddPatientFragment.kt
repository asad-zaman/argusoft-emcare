package com.argusoft.who.emcare.ui.home.patient.add

import androidx.core.os.bundleOf
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentAddPatientBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.patient.PatientViewModel
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.google.android.fhir.datacapture.QuestionnaireFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddPatientFragment : BaseFragment<FragmentAddPatientBinding>() {

    private val patientViewModel: PatientViewModel by viewModels()
    private val questionnaireFragment = QuestionnaireFragment()

    override fun initView() {
        setupToolbar()
        addQuestionnaireFragment()
    }

    private fun setupToolbar() {
        binding.headerLayout.toolbar.setTitleAndBack(R.string.title_add_patient)
        binding.headerLayout.toolbar.inflateMenu(R.menu.menu_save)
        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            patientViewModel.questionnaireJson?.let {
                patientViewModel.savePatient(questionnaireFragment.getQuestionnaireResponse(), it)
            }
            return@setOnMenuItemClickListener true
        }
    }

    private fun addQuestionnaireFragment() {
        patientViewModel.questionnaireJson = requireContext().assets.open("questionnaire-EmCareA.json").bufferedReader().use {
            it.readText()
        }
        patientViewModel.questionnaireJson?.let {
            questionnaireFragment.arguments = bundleOf(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE to it)
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
    }
}