package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.Dashboard
import com.argusoft.who.emcare.ui.home.patient.PatientAdapter
import com.argusoft.who.emcare.ui.home.patient.PatientViewModel
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.*
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import com.google.android.fhir.sync.State
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(), SearchView.OnQueryTextListener {

    private lateinit var glideRequests: GlideRequests
    private val syncViewModel: SyncViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val patientViewModel: PatientViewModel by viewModels()
    private lateinit var patientAdapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        patientAdapter = PatientAdapter(onClickListener = this)
        patientViewModel.getPatients("", preference.getLoggedInUser()?.location?.get(0)?.id, patientAdapter.isNotEmpty())
    }

    override fun initView() {
        binding.headerLayout.toolbar.setUpDashboard()
        if (preference.getLoggedInUser() != null) binding.nameTextView.text = preference.getLoggedInUser()?.userName
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.progressLayout.recyclerView = binding.recyclerView
        binding.progressLayout.swipeRefreshLayout = binding.swipeRefreshLayout
        binding.recyclerView.adapter = patientAdapter
        binding.progressLayout.setOnSwipeRefreshLayout {
            patientViewModel.getPatients(binding.searchView.query.toString(), preference.getLoggedInUser()?.location?.get(0)?.id, true)
        }
    }

    override fun initListener() {
        binding.searchView.setOnQueryTextListener(this)
        binding.addPatientButton.setOnClickListener(this)
        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sync -> {
                    syncViewModel.syncPatients()
                }
            }
            return@setOnMenuItemClickListener true
        }

    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        patientViewModel.getPatients(binding.searchView.query.toString(), preference.getLoggedInUser()?.location?.get(0)?.id, patientAdapter.isNotEmpty())
        return true
    }

    override fun initObserver() {
        observeNotNull(syncViewModel.syncState) {
            when (it) {
                is State.Started -> {
                    var message = getString(R.string.msg_sync_started)
                    settingsViewModel.languageApiState.value?.whenSuccess {
                        it.languageData?.convertToMap()?.apply {
                            message = getOrElse("Sync_started") { getString(R.string.msg_sync_started) }
                        }
                    }
                    requireContext().showToast(message = message)
                }
                is State.Finished -> {
                    var message = getString(R.string.msg_sync_successfully)
                    settingsViewModel.languageApiState.value?.whenSuccess {
                        it.languageData?.convertToMap()?.apply {
                            message = getOrElse("Sync_Successful") { getString(R.string.msg_sync_successfully) }
                        }
                    }
                    requireContext().showToast(message = message)
                }
                is State.Failed -> {
                    var message = getString(R.string.msg_sync_failed)
                    settingsViewModel.languageApiState.value?.whenSuccess {
                        it.languageData?.convertToMap()?.apply {
                            message = getOrElse("Sync_failed") { getString(R.string.msg_sync_failed) }
                        }
                    }
                    requireContext().showToast(message = message)
                }
            }
        }
        observeNotNull(settingsViewModel.languageApiState) {
            it.whenSuccess {
                it.languageData?.convertToMap()?.apply {
                    binding.welcomeTextView.text = getOrElse("Welcome") { getString(R.string.label_welcome) }
                }
            }
        }

        observeNotNull(patientViewModel.patients) { apiResponse ->
            apiResponse.handleListApiView(binding.progressLayout, skipIds = listOf(R.id.searchView, R.id.addPatientButton, R.id.swipeRefreshLayout)) {
                it?.let { list ->
                    patientAdapter.clearAllItems()
                    patientAdapter.addAll(list)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.addPatientButton -> {
                navigate(R.id.action_homeFragment_to_addPatientFragment)
            }
        }
    }
}