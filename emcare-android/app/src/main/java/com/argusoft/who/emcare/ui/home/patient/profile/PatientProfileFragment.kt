package com.argusoft.who.emcare.ui.home.patient.profile

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientProfileBinding
import com.argusoft.who.emcare.sync.SyncState
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class PatientProfileFragment : BaseFragment<FragmentPatientProfileBinding>() {

    private val patientProfileViewModel: PatientProfileViewModel by viewModels()
    private val syncViewModel: SyncViewModel by viewModels()
    private lateinit var activeConsultationsAdapter: PatientProfileActiveConsultationsAdapter
    private lateinit var previousConsultationsAdapter: PatientProfilePreviousConsultationsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activeConsultationsAdapter = PatientProfileActiveConsultationsAdapter()
        previousConsultationsAdapter = PatientProfilePreviousConsultationsAdapter()
    }

    override fun onResume() {
        super.onResume()
        activeConsultationsAdapter.clearAllItems() // Added to remove duplicate consultations on coming back to this screen.
        previousConsultationsAdapter.clearAllItems()
        setupActiveConsultationsRecyclerView()
        setupPreviousConsultationsRecyclerView()
    }

    override fun initView() {
        binding.headerLayout.toolbar.setTitleDashboard(getString(R.string.title_patient_profile))
        patientProfileViewModel.getLastConsultationDate(requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!)
        binding.nameTextView.setText(requireArguments().getString(INTENT_EXTRA_PATIENT_NAME))
        val dateOfBirth = requireArguments().getString(INTENT_EXTRA_PATIENT_DOB)
        if(dateOfBirth != null && !dateOfBirth.equals("Not Provided", true) && dateOfBirth.isNotBlank()){
            val oldFormatDate = SimpleDateFormat("YYYY-MM-DD").parse(dateOfBirth)
            binding.dobTextView.text = SimpleDateFormat(DATE_FORMAT).format(oldFormatDate!!)
        } else {
            binding.dobTextView.text = "Not Provided"
        }
    }

    private fun setupActiveConsultationsRecyclerView() {
        binding.activePatientProgressLayout.recyclerView = binding.activeConsultationRecyclerView
        binding.activeConsultationRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.activeConsultationRecyclerView.adapter = activeConsultationsAdapter
        patientProfileViewModel.getActiveConsultations(requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!)
    }

    private fun setupPreviousConsultationsRecyclerView() {
        binding.previousPatientProgressLayout.recyclerView = binding.previousConsultationRecyclerView
        binding.previousConsultationRecyclerView.addItemDecoration(
            DividerItemDecoration(
                context,
                LinearLayoutManager.VERTICAL
            )
        )
        binding.previousConsultationRecyclerView.adapter = previousConsultationsAdapter
        patientProfileViewModel.getPreviousConsultations(requireArguments().getString(INTENT_EXTRA_PATIENT_ID)!!)
    }



    override fun initListener() {
        binding.newConsultationButton.setOnClickListener(this)

        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sync -> {
                    syncViewModel.syncPatients()
                }
                R.id.action_more -> {
                    (activity as HomeActivity).openDrawer()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun initObserver() {
        observeNotNull(syncViewModel.syncState) { apiResponse ->
            apiResponse.whenLoading {
                binding.patientProfileLayout.showHorizontalProgress()
                requireContext().showSnackBar(
                    view = binding.patientProfileLayout,
                    message = getString(R.string.msg_sync_started),
                    duration = Snackbar.LENGTH_INDEFINITE,
                    isError = false
                )
            }
            apiResponse.handleListApiView(binding.patientProfileLayout) {
                when (it) {
                    is SyncJobStatus.InProgress -> {
                        //Code to show text.
                        //Reference: https://github.com/google/android-fhir/blob/master/demo/src/main/java/com/google/android/fhir/demo/PatientListFragment.kt
                    }

                    is SyncJobStatus.Finished -> {
                        requireContext().showSnackBar(
                            view = binding.patientProfileLayout,
                            message = getString(R.string.msg_sync_successfully),
                            duration = Snackbar.LENGTH_SHORT,
                            isError = false
                        )
                    }
                    is SyncJobStatus.Failed -> {
                        binding.patientProfileLayout.showContent()
                        requireContext().showSnackBar(
                            view = binding.patientProfileLayout,
                            message = getString(R.string.msg_sync_failed),
                            duration = Snackbar.LENGTH_SHORT,
                            isError = true
                        )
                    }
                }
            }
        }

        observeNotNull(patientProfileViewModel.activeConsultations) { apiResponse ->
            apiResponse.handleListApiView(binding.activePatientProgressLayout) {
                it?.let { list ->
                    activeConsultationsAdapter.clearAllItems()
                    activeConsultationsAdapter.addAll(list)
                }
            }
        }

        observeNotNull(patientProfileViewModel.previousConsultations) { apiResponse ->
            apiResponse.handleListApiView(binding.previousPatientProgressLayout) {
                it?.let { list ->
                    previousConsultationsAdapter.clearAllItems()
                    previousConsultationsAdapter.addAll(list)
                }
            }
        }

        observeNotNull(patientProfileViewModel.lastConsultationDate) { apiResponse ->
            apiResponse.whenSuccess {
                if(it != null && it.isNotBlank()) {
                    val oldFormatDate = SimpleDateFormat("yyyy-MM-dd").parse(it.substringBefore("T"))
                    binding.lastConsultationDateTextView.text = SimpleDateFormat(DATE_FORMAT).format(oldFormatDate!!)
                } else {
                    binding.lastConsultationDateTextView.text = "Not Provided"
                }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.newConsultationButton -> {
                navigate(R.id.action_patientProfileFragment_to_patientQuestionnaireFragment) {
                    putString(
                        INTENT_EXTRA_QUESTIONNAIRE_ID,
                        stageToQuestionnaireId[consultationFlowStageList[1]]
                    )
                    putString(
                        INTENT_EXTRA_STRUCTUREMAP_ID,
                        stageToStructureMapId[consultationFlowStageList[1]]
                    )
//                    putString(INTENT_EXTRA_QUESTIONNAIRE_HEADER, stageToBadgeMap[consultationFlowStageList[1]])
                    putString(
                        INTENT_EXTRA_QUESTIONNAIRE_HEADER,
                        stageToBadgeMap[consultationFlowStageList[1]]
                    ) //For testing only replace it with badgeText
                    putString(
                        INTENT_EXTRA_PATIENT_ID,
                        requireArguments().getString(INTENT_EXTRA_PATIENT_ID)
                    )
                    putString(INTENT_EXTRA_ENCOUNTER_ID, UUID.randomUUID().toString())
                    putString(INTENT_EXTRA_CONSULTATION_STAGE, consultationFlowStageList[1])
                    putBoolean(INTENT_EXTRA_IS_ACTIVE, true)
                }
            }
        }
    }

}