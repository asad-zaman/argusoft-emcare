package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.settings.SettingsViewModel
import com.argusoft.who.emcare.utils.extention.*
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import com.google.android.fhir.sync.State
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    private lateinit var glideRequests: GlideRequests
    private val syncViewModel: SyncViewModel by viewModels()
    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var homePagerAdapter: HomePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
    }

    override fun initView() {
        binding.headerLayout.toolbar.setTitleDashboard(id = getString(R.string.title_home))
        homePagerAdapter = HomePagerAdapter(this, PatientListFragment(), ConsultationListFragment())
        binding.viewPager2.adapter = homePagerAdapter
        binding.viewPager2.isUserInputEnabled = false
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(homeViewModel.currentTab))
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

        binding.tabLayout.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.viewPager2.currentItem = tab?.position!!
                homeViewModel.currentTab = tab.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                binding.viewPager2.currentItem = tab?.position!!
                homeViewModel.currentTab = tab.position
            }

        })

    }

    override fun onResume() {
        super.onResume()
        homeViewModel.currentTab = 0
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(homeViewModel.currentTab))
        (activity as? HomeActivity)?.closeSidepane()
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