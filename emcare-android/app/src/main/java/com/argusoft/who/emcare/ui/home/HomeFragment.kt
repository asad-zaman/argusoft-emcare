package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.Dashboard
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.SpacesItemDecoration
import com.argusoft.who.emcare.utils.extention.*
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import com.google.android.fhir.sync.State
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val syncViewModel: SyncViewModel by viewModels()
    private lateinit var glideRequests: GlideRequests
    private lateinit var homeAdapter: HomeAdapter
    private val settingsViewModel: SettingsViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        homeAdapter = HomeAdapter(onClickListener = this)
    }

    override fun initView() {
        binding.headerLayout.toolbar.setUpDashboard()
        if (preference.getLoggedInUser() != null) binding.nameTextView.text = preference.getLoggedInUser()?.userName
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(20))
        binding.recyclerView.adapter = homeAdapter
    }

    override fun initListener() {
        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sync -> {
                    syncViewModel.syncPatients()
                }
            }
            return@setOnMenuItemClickListener true
        }
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
                    (activity as? HomeActivity)?.refreshLanguages()
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
                    val dashboardList = arrayListOf<Dashboard?>(
                        Dashboard(getOrElse("Registration") { "Registration" }, "#F6D1CB", "#9c5950", R.drawable.ic_registration),
                        Dashboard(getOrElse("Risk_Assessment") { "Risk Assessment" }, "#AFE9ED", "#478c91", R.drawable.ic_risk_assessment),
                        Dashboard(getOrElse("Referral") { "Referral" }, "#B9DDF5", "#5788ac", R.drawable.ic_referral),
                        Dashboard(getOrElse("Notification") { "Notification" }, "#DFD1F5", "#6d558a", R.drawable.ic_dashboard_notification),
                        Dashboard(getOrElse("Reports") { "Reports" }, "#FCE1C4", "#82603e", R.drawable.ic_reports),
                        Dashboard(getOrElse("Announcements") { "Announcements" }, "#C1DBD2", "#48816f", R.drawable.ic_announcements),
                    )
                    if (homeAdapter.getItemsList().isEmpty()) {
                        homeAdapter.addAll(dashboardList)
                    }
                    binding.welcomeTextView.text = getOrElse("Welcome") { getString(R.string.label_welcome) }
                    binding.titleTextView.text = getOrElse("Home_title") { getString(R.string.label_what_would_you_like_to_do_today) }
                }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.itemRootLayout -> {
                when (view.tag as? Int) {
                    0 -> {
                        navigate(R.id.action_homeFragment_to_locationFragment)
                    }
                }
            }
        }
    }
}