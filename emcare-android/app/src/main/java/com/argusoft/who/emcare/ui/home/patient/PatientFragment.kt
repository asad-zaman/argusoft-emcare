package com.argusoft.who.emcare.ui.home.patient

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_LOCATION_ID
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.sync.State
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientFragment : BaseFragment<FragmentPatientBinding>(), SearchView.OnQueryTextListener {

    private val patientViewModel: PatientViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val syncViewModel: SyncViewModel by viewModels()
    private lateinit var patientAdapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientAdapter = PatientAdapter(onClickListener = this)
        patientViewModel.getPatients("", requireArguments().getInt(INTENT_EXTRA_LOCATION_ID), patientAdapter.isNotEmpty())
    }

    override fun initView() {
        binding.headerLayout.toolbar.inflateMenu(R.menu.patient_menu)
        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            syncViewModel.syncPatients()
            return@setOnMenuItemClickListener true
        }
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.progressLayout.recyclerView = binding.recyclerView
        binding.progressLayout.swipeRefreshLayout = binding.swipeRefreshLayout
        binding.recyclerView.adapter = patientAdapter
        binding.progressLayout.setOnSwipeRefreshLayout {
            patientViewModel.getPatients(binding.searchView.query.toString(), requireArguments().getInt(INTENT_EXTRA_LOCATION_ID), true)
        }
    }

    override fun initListener() {
        binding.searchView.setOnQueryTextListener(this)
        binding.addPatientButton.setOnClickListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        patientViewModel.getPatients(binding.searchView.query.toString(), requireArguments().getInt(INTENT_EXTRA_LOCATION_ID), patientAdapter.isNotEmpty())
        return true
    }

    override fun initObserver() {
        observeNotNull(patientViewModel.patients) { apiResponse ->
            apiResponse.handleListApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout, R.id.addPatientButton, R.id.searchView)) {
                it?.let { list ->
                    patientAdapter.clearAllItems()
                    patientAdapter.addAll(list)
                }
            }
        }
        observeNotNull(syncViewModel.syncState) {
            when (it) {
                is State.Started -> requireContext().showToast(messageResId = R.string.msg_sync_started)
                is State.Finished -> requireContext().showToast(messageResId = R.string.msg_sync_successfully)
                is State.Failed -> requireContext().showToast(messageResId = R.string.msg_sync_failed)
            }
        }
        observeNotNull(settingsViewModel.languageApiState) {
            it.whenSuccess {
                it.languageData?.convertToMap()?.apply {
                    binding.addPatientButton.text = getOrElse("Add_Patient") { getString(R.string.button_add_patient) }
                    binding.searchView.queryHint = getOrElse("Find_by_Patient_Name") { getString(R.string.query_hint_patient_search) }
                    binding.headerLayout.toolbar.setTitleAndBack(getOrElse("Registered_Patients") { getString(R.string.title_patient) } )
                }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.addPatientButton -> {
                navigate(R.id.action_patientFragment_to_addPatientFragment) {
                    putInt(INTENT_EXTRA_LOCATION_ID, requireArguments().getInt(INTENT_EXTRA_LOCATION_ID))
                }
            }
        }
    }
}