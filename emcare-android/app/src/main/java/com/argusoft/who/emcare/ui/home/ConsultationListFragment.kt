package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentConsultationListBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.ConsultationItemData
import com.argusoft.who.emcare.utils.extention.handleListApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ConsultationListFragment: BaseFragment<FragmentConsultationListBinding>(), SearchView.OnQueryTextListener {
    private lateinit var glideRequests: GlideRequests
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var consultationAdapter: ConsultationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        consultationAdapter = ConsultationAdapter(onClickListener = this, diffCallBack = ConsultationItemComparator)
    }

    override fun onResume() {
        super.onResume()
        viewLifecycleOwner.lifecycleScope.launch {
            consultationAdapter.refresh()
            homeViewModel.getConsultationItems().collectLatest { pagingData ->
                binding.progressLayout.swipeRefreshLayout?.isRefreshing = false
                consultationAdapter.submitData(pagingData)
            }

        }
        (this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.setOnQueryTextListener(this)
    }
    override fun initView() {
        setupRecyclerView()
    }

    override fun initListener() {
        // No Listener required
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        val search: String = (this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.query.toString()
        viewLifecycleOwner.lifecycleScope.launch {
            consultationAdapter.refresh()
            homeViewModel.getConsultationItems(search).collectLatest { pagingData ->
                consultationAdapter.submitData(pagingData)
            }

        }
        return true
    }


    override fun initObserver() {
        observeNotNull(homeViewModel.consultations) { apiResponse ->
            apiResponse.handleListApiView(binding.progressLayout, skipIds = listOf(R.id.searchView, R.id.swipeRefreshLayout)) {
                it?.let { list ->
                    consultationAdapter.clearAllItems()
                    consultationAdapter.addAll(list)
                }
            }
        }
    }

    private fun setupRecyclerView() {
        binding.progressLayout.recyclerView = binding.recyclerView
        binding.progressLayout.swipeRefreshLayout = binding.swipeRefreshLayout
        binding.recyclerView.adapter = consultationAdapter
        binding.progressLayout.setOnSwipeRefreshLayout {
            viewLifecycleOwner.lifecycleScope.launch {
                consultationAdapter.refresh()
                homeViewModel.getConsultationItems().collectLatest { pagingData ->
                    binding.progressLayout?.swipeRefreshLayout?.isRefreshing = false
                    consultationAdapter.submitData(pagingData)
                }
            }
        }
    }

    object ConsultationItemComparator : DiffUtil.ItemCallback<ConsultationItemData>() {
        override fun areItemsTheSame(oldItem: ConsultationItemData, newItem: ConsultationItemData): Boolean {
            // Id is unique.
            return oldItem.consultationFlowItemId == newItem.consultationFlowItemId
                    && oldItem.consultationStage == newItem.consultationStage
        }

        override fun areContentsTheSame(oldItem: ConsultationItemData, newItem: ConsultationItemData): Boolean {
            return oldItem == newItem
        }
    }

}