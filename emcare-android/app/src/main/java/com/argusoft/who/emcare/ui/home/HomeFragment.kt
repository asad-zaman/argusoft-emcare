package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.dashboardList
import com.argusoft.who.emcare.utils.SpacesItemDecoration
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var glideRequests: GlideRequests
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        homeAdapter = HomeAdapter(dashboardList, onClickListener = this)
    }

    override fun initView() {
        binding.headerLayout.toolbar.setUpDashboard()
        if(preference.getLoggedInuser() != null) binding.nameTextView.text = preference.getLoggedInuser()?.userName
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.recyclerView.addItemDecoration(SpacesItemDecoration(20))
        binding.recyclerView.adapter = homeAdapter
    }

    override fun initListener() {

    }

    override fun initObserver() {

    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.itemRootLayout -> {
                when (view.tag as? Int) {
                    0 -> {
                        navigate(R.id.action_homeFragment_to_patientFragment)
                    }
                }
            }
        }
    }
}