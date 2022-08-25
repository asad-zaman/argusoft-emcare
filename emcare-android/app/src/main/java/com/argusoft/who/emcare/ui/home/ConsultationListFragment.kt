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
        consultationAdapter = ConsultationAdapter(homeViewModel.getConsultations())
    }

    override fun initView() {
        setupRecyclerView()
        //Reasons for consultations Adapter
        val reasonsAdapter: ArrayAdapter<String> = ArrayAdapter(context!!, R.layout.spinner_list_item,
            listOf("Reason for Consultation", "Sick Child", "Well Child", "Immunizations"))
//        binding.consultationSpinner.adapter = reasonsAdapter
    }

    override fun initListener() {
        (this.parentFragment)?.view?.findViewById<SearchView>(R.id.searchView)?.setOnQueryTextListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        consultationAdapter.clearAllItems()
        consultationAdapter.addAll(homeViewModel.getConsultations())
        return true
    }


    override fun initObserver() {
    }

    private fun setupRecyclerView() {
        binding.progressLayout.recyclerView = binding.recyclerView
        binding.progressLayout.swipeRefreshLayout = binding.swipeRefreshLayout
        binding.recyclerView.adapter = consultationAdapter
        binding.progressLayout.setOnSwipeRefreshLayout {
            consultationAdapter.clearAllItems()
            consultationAdapter.addAll(homeViewModel.getConsultations())
        }
    }

}