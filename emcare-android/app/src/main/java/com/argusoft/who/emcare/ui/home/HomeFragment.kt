package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    private lateinit var glideRequests: GlideRequests
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var homePagerAdapter: HomePagerAdapter

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
        initSyncAndMoreMenuItemListener(binding.headerLayout.toolbar)

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