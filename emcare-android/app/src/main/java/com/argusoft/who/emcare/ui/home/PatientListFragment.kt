package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientListBinding
import com.argusoft.who.emcare.ui.common.INTENT_EXTRA_FACILITY_ID
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PatientListFragment: BaseFragment<FragmentPatientListBinding>(), SearchView.OnQueryTextListener {
    private lateinit var glideRequests: GlideRequests
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var homeAdapter: HomeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        homeAdapter = HomeAdapter(onClickListener = this, diffCallBack = PatientItemComparator)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            homeAdapter.refresh()
            homeViewModel.getPatientItems(preference.getLoggedInUser()?.facility?.get(0)?.facilityId).collectLatest { pagingData ->
                homeAdapter.submitData(pagingData)
            }
        }
        (this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.setOnQueryTextListener(this)
    }
    override fun initView() {
        setupRecyclerView()
    }

    override fun initListener() {
        binding.addPatientButton.setOnClickListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val search: String = (this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.query.toString()
        viewLifecycleOwner.lifecycleScope.launch {
            homeAdapter.refresh()
            homeViewModel.getPatientItems(preference.getLoggedInUser()?.facility?.get(0)?.facilityId, search).collectLatest { pagingData ->
                binding.progressLayout.swipeRefreshLayout?.isRefreshing = false
                homeAdapter.submitData(pagingData)
            }
        }
        return true
    }


    override fun initObserver() {
//        observeNotNull(homeViewModel.patients) { apiResponse ->
//            apiResponse.handleListApiView(binding.progressLayout, skipIds = listOf(R.id.searchView, R.id.addPatientButton, R.id.swipeRefreshLayout)) {
//                it?.let { list ->
//                    homeAdapter.clearAllItems()
//                    homeAdapter.addAll(list)
//                }
//            }
//        }
    }

    private fun setupRecyclerView() {
        binding.progressLayout.recyclerView = binding.recyclerView
        binding.progressLayout.swipeRefreshLayout = binding.swipeRefreshLayout
        binding.recyclerView.adapter = homeAdapter
        binding.progressLayout.setOnSwipeRefreshLayout {
            viewLifecycleOwner.lifecycleScope.launch {
                homeAdapter.refresh()
                homeViewModel.getPatientItems(preference.getLoggedInUser()?.facility?.get(0)?.facilityId).collectLatest { pagingData ->
                    binding.progressLayout.swipeRefreshLayout?.isRefreshing = false
                    homeAdapter.submitData(pagingData)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.addPatientButton -> {
                navigate(R.id.action_homeFragment_to_addPatientFragment) {
                    putString(INTENT_EXTRA_FACILITY_ID, preference.getLoggedInUser()?.facility?.get(0)?.facilityId)
                }
            }
        }
    }

    object PatientItemComparator : DiffUtil.ItemCallback<PatientItem>() {
        override fun areItemsTheSame(oldItem: PatientItem, newItem: PatientItem): Boolean {
            // Id is unique.
            return oldItem.resourceId == newItem.resourceId
        }

        override fun areContentsTheSame(oldItem: PatientItem, newItem: PatientItem): Boolean {
            return oldItem == newItem
        }
    }
}