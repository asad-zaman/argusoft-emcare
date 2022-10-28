package com.argusoft.who.emcare.ui.home.patient.profile

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientProfileBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.*
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.sync.State
import dagger.hilt.android.AndroidEntryPoint
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

    override fun initView() {
        binding.headerLayout.toolbar.setTitleDashboard(getString(R.string.title_patient_profile))
        setupActiveConsultationsRecyclerView()
        setupPreviousConsultationsRecyclerView()
        binding.nameTextView.text = requireArguments().getString(INTENT_EXTRA_PATIENT_NAME)
        binding.dobTextView.text = requireArguments().getString(INTENT_EXTRA_PATIENT_DOB)
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
        observeNotNull(syncViewModel.syncState) {
            when (it) {
                is State.Started -> {
                    val message = getString(R.string.msg_sync_started)
                    requireContext().showToast(message = message)
                }
                is State.Finished -> {
                    val message = getString(R.string.msg_sync_successfully)
                    requireContext().showToast(message = message)
                }
                is State.Failed -> {
                    val message = getString(R.string.msg_sync_failed)
                    requireContext().showToast(message = message)
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