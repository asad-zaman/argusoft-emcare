package com.argusoft.who.emcare.ui.home

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.auth.login.LoginViewModel
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.*
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    private lateinit var glideRequests: GlideRequests
    private val loginViewModel: LoginViewModel by viewModels()
    private val syncViewModel: SyncViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var homePagerAdapter: HomePagerAdapter
    private val formatString12 = "dd/MM/yyyy hh:mm:ss a"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
    }

    override fun initView() {
        binding.headerLayout.toolbar.setTitleDashboard(id = getString(R.string.title_home) + " " + preference.getCountry())
        homePagerAdapter = HomePagerAdapter(this, PatientListFragment(), ConsultationListFragment())
        binding.viewPager2.adapter = homePagerAdapter
        binding.viewPager2.isUserInputEnabled = false
    }

    override fun initListener() {
        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sync -> {
                    syncViewModel.syncPatients(true)
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
                //Empty Block
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                onTabSelected(tab)
            }

        })

    }

    override fun onResume() {
        super.onResume()
        binding.tabLayout.selectTab(binding.tabLayout.getTabAt(homeViewModel.currentTab))
        (activity as? HomeActivity)?.closeSidepane()
    }

    override fun initObserver() {

        initObserverSync(binding.rootLayout, false)
    }
}