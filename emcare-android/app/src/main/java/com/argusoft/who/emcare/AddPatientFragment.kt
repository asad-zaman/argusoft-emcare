package com.argusoft.who.emcare

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.fhir.datacapture.QuestionnaireFragment
import org.hl7.fhir.r4.model.QuestionnaireResponse

/** A fragment class to show patient registration screen. */
class AddPatientFragment : Fragment(R.layout.add_patient_fragment) {

  private val viewModel: AddPatientViewModel by viewModels()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    setUpActionBar()
    setHasOptionsMenu(true)
    updateArguments()
    if (savedInstanceState == null) {
      addQuestionnaireFragment()
    }
    observePatientSaveAction()
    (activity as MainActivity).setDrawerEnabled(false)
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    super.onCreateOptionsMenu(menu, inflater)
    inflater.inflate(R.menu.add_patient_fragment_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      R.id.action_add_patient_submit -> {
        onSubmitAction()
        true
      }
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  private fun setUpActionBar() {
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = requireContext().getString(R.string.add_patient)
      setDisplayHomeAsUpEnabled(true)
    }
  }

  private fun updateArguments() {
    requireArguments()
//      .putString(QUESTIONNAIRE_FILE_PATH_KEY, "new-patient-registration-paginated.json")
      .putString(QUESTIONNAIRE_FILE_PATH_KEY, "questionnaire-EmCareA.json")
  }

  private fun addQuestionnaireFragment() {
    val fragment = QuestionnaireFragment()
    fragment.arguments =
      bundleOf(QuestionnaireFragment.BUNDLE_KEY_QUESTIONNAIRE to viewModel.questionnaire)
    childFragmentManager.commit {
      add(R.id.add_patient_container, fragment, QUESTIONNAIRE_FRAGMENT_TAG)
    }
  }

  private fun onSubmitAction() {
    val questionnaireFragment =
      childFragmentManager.findFragmentByTag(QUESTIONNAIRE_FRAGMENT_TAG) as QuestionnaireFragment
    savePatient(questionnaireFragment.getQuestionnaireResponse())
  }

  private fun savePatient(questionnaireResponse: QuestionnaireResponse) {
    viewModel.savePatient(questionnaireResponse)
  }

  private fun observePatientSaveAction() {
    viewModel.isPatientSaved.observe(viewLifecycleOwner) {
      if (!it) {
        Toast.makeText(requireContext(), "Inputs are missing.", Toast.LENGTH_SHORT).show()
        return@observe
      }
      Toast.makeText(requireContext(), "Patient is saved.", Toast.LENGTH_SHORT).show()
      NavHostFragment.findNavController(this).navigateUp()
    }
  }

  companion object {
    const val QUESTIONNAIRE_FILE_PATH_KEY = "questionnaire-file-path-key"
    const val QUESTIONNAIRE_FRAGMENT_TAG = "questionnaire-fragment-tag"
  }
}
