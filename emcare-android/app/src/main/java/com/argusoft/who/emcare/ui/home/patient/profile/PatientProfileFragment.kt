package com.argusoft.who.emcare.ui.home.patient.profile

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientProfileBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_DOB
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_PATIENT_NAME
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_QUESTIONNAIRE_HEADER
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.convertToMap
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.showToast
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.google.android.fhir.sync.State
import dagger.hilt.android.AndroidEntryPoint


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
        binding.activeConsultationRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        binding.activeConsultationRecyclerView.adapter = activeConsultationsAdapter
        activeConsultationsAdapter.clearAllItems()
        activeConsultationsAdapter.addAll(patientProfileViewModel.getActiveConsultations())

    }

    private fun setupPreviousConsultationsRecyclerView() {
//        var divider: DividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.HORIZONTAL)
//        divider.setDrawable(ContextCompat.getDrawable(context!!, R.drawable.custom_divider)!!)
        binding.previousConsultationRecyclerView.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
        binding.previousConsultationRecyclerView.adapter = previousConsultationsAdapter
        previousConsultationsAdapter.clearAllItems()
        previousConsultationsAdapter.addAll(patientProfileViewModel.getPreviousConsultations())
    }

    override fun initListener() {
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
    }

}