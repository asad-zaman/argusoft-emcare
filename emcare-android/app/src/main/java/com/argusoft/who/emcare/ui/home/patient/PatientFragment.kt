package com.argusoft.who.emcare.ui.home.patient

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentPatientBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.handleListApiView
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.observeNotNull
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PatientFragment : BaseFragment<FragmentPatientBinding>(), SearchView.OnQueryTextListener {

    private val patientViewModel: PatientViewModel by viewModels()
    private lateinit var patientAdapter: PatientAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        patientAdapter = PatientAdapter(onClickListener = this)
    }

    override fun initView() {
        binding.headerLayout.toolbar.setTitleAndBack(R.string.title_patient)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        binding.progressLayout.recyclerView = binding.recyclerView
        binding.progressLayout.swipeRefreshLayout = binding.swipeRefreshLayout
        binding.recyclerView.adapter = patientAdapter
        binding.progressLayout.setOnSwipeRefreshLayout {
            patientViewModel.getPatients(binding.searchView.query.toString(), true)
        }
    }

    override fun initListener() {
        binding.searchView.setOnQueryTextListener(this)
        binding.addPatientButton.setOnClickListener(this)
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        patientViewModel.getPatients(binding.searchView.query.toString(), patientAdapter.isNotEmpty())
        return true
    }

    override fun initObserver() {
        observeNotNull(patientViewModel.patients) { apiResponse ->
            apiResponse.handleListApiView(binding.progressLayout, skipIds = listOf(R.id.headerLayout, R.id.addPatientButton, R.id.searchView)) {
                it?.let { list ->
                    patientAdapter.clearAllItems()
                    patientAdapter.addAll(list)
                }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.addPatientButton -> {
                navigate(R.id.action_patientFragment_to_addPatientFragment)
            }
        }
    }
}