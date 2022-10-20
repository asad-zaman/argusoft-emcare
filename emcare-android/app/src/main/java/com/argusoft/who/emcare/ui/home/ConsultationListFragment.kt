package com.argusoft.who.emcare.ui.home

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentConsultationListBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.handleListApiView
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ConsultationListFragment: BaseFragment<FragmentConsultationListBinding>(), SearchView.OnQueryTextListener {
    private lateinit var glideRequests: GlideRequests
    private val homeViewModel: HomeViewModel by viewModels()
    private lateinit var consultationAdapter: ConsultationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
        consultationAdapter = ConsultationAdapter(onClickListener = this)
    }

    override fun onResume() {
        super.onStart()
        homeViewModel.getConsultations((this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.query.toString(), consultationAdapter.isNotEmpty())
        (this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.setOnQueryTextListener(this)
    }
    override fun initView() {
        setupRecyclerView()
    }

    override fun initListener() {
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        homeViewModel.getConsultations((this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.query.toString(), consultationAdapter.isNotEmpty())
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
            homeViewModel.getConsultations((this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.query.toString(), consultationAdapter.isNotEmpty())
        }
    }

}