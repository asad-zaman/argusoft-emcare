package com.argusoft.who.emcare.oldstruct

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.argusoft.who.emcare.EmCareApplication
import com.argusoft.who.emcare.R
import com.google.android.fhir.FhirEngine
import com.argusoft.who.emcare.databinding.PatientDetailBinding

/**
 * A fragment representing a single Patient detail screen. This fragment is contained in a
 * [MainActivity].
 */
class PatientDetailsFragment : Fragment() {
  private lateinit var fhirEngine: FhirEngine
  private lateinit var patientDetailsViewModel: PatientDetailsViewModel
  private val args: PatientDetailsFragmentArgs by navArgs()
  private var _binding: PatientDetailBinding? = null
  private val binding
    get() = _binding!!

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setHasOptionsMenu(true)
  }

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    _binding = PatientDetailBinding.inflate(inflater, container, false)
    return binding.root
  }

  @RequiresApi(Build.VERSION_CODES.O)
  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)
    fhirEngine = EmCareApplication.fhirEngine(requireContext())
    patientDetailsViewModel =
      ViewModelProvider(
          this,
          PatientDetailsViewModelFactory(requireActivity().application, fhirEngine, args.patientId)
        )
        .get(PatientDetailsViewModel::class.java)
    val adapter = PatientDetailsRecyclerViewAdapter(::onDeletePatientClick)
    binding.recycler.adapter = adapter
    (requireActivity() as AppCompatActivity).supportActionBar?.apply {
      title = "Patient Card"
      setDisplayHomeAsUpEnabled(true)
    }
    patientDetailsViewModel.livePatientData.observe(viewLifecycleOwner) { adapter.submitList(it) }
    patientDetailsViewModel.getPatientDetailData()
    (activity as MainActivity).setDrawerEnabled(false)
  }

  private fun onDeletePatientClick() {
    val builder = AlertDialog.Builder(requireContext())
    builder.setMessage("Are you sure you want to Delete?")
      .setCancelable(false)
      .setPositiveButton("Yes") { dialog, id ->
        // Delete selected patient from the database
        patientDetailsViewModel.deletePatient()
        Toast.makeText(requireContext(), "Patient is deleted.", Toast.LENGTH_SHORT).show()
        findNavController()
          .navigate(
            PatientDetailsFragmentDirections.actionPatientDetailsToPatientList()
          )
      }
      .setNegativeButton("No") { dialog, id ->
        // Dismiss the dialog
        dialog.dismiss()
      }
    val alert = builder.create()
    alert.show()
  }

  override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
    inflater.inflate(R.menu.details_options_menu, menu)
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    return when (item.itemId) {
      android.R.id.home -> {
        NavHostFragment.findNavController(this).navigateUp()
        true
      }
      R.id.menu_patient_edit -> {
        findNavController()
          .navigate(PatientDetailsFragmentDirections.navigateToEditPatient(args.patientId))
        true
      }
      else -> super.onOptionsItemSelected(item)
    }
  }

  override fun onDestroyView() {
    super.onDestroyView()
    _binding = null
  }
}
