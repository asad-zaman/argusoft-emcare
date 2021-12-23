package com.argusoft.who.emcare.ui.home.location

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentLocationBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LocationFragment : BaseFragment<FragmentLocationBinding>() {

    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var locationAdapter: LocationAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        locationAdapter = LocationAdapter()
    }

    override fun initView() {
        binding.headerLayout.toolbar.setTitleAndBack(R.string.title_select_location)
        setupRecyclerView()
        TODO("Not yet implemented")
    }

    private fun setupRecyclerView() {
        binding.selectLocationRecyclerView.adapter = locationAdapter
    }

    override fun initListener() {
        binding.submitLocationButton.setOnClickListener(this)
    }

    override fun initObserver() {
        TODO("Not yet implemented")
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.submitLocationButton -> {
                TODO("Save location method")
            }
        }
    }
}