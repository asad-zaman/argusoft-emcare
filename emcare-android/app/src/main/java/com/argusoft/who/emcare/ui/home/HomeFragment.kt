package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.dashboardList
import com.argusoft.who.emcare.ui.home.patient.PatientViewModel
import com.argusoft.who.emcare.utils.SpacesItemDecoration
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.showToast
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import com.google.android.fhir.sync.State
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>() {

    private val patientViewModel: PatientViewModel by viewModels()
    private lateinit var glideRequests: GlideRequests
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        homeAdapter = HomeAdapter(dashboardList, onClickListener = this)
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
                    patientViewModel.syncPatients()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun initObserver() {
        observeNotNull(patientViewModel.syncState) {
            when (it) {
                is State.Started -> requireContext().showToast(messageResId = R.string.msg_sync_started)
                is State.Finished -> requireContext().showToast(messageResId = R.string.msg_sync_successfully)
                is State.Failed -> requireContext().showToast(messageResId = R.string.msg_sync_failed)
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