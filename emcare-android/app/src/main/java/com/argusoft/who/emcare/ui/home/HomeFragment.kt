package com.argusoft.who.emcare.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentHomeBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.*
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.utils.glide.GlideRequests
import com.google.android.fhir.sync.ResourceSyncException
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint
import java.time.OffsetDateTime
import kotlin.math.roundToInt

@AndroidEntryPoint
class HomeFragment : BaseFragment<FragmentHomeBinding>(){

    private lateinit var glideRequests: GlideRequests
    private val syncViewModel: SyncViewModel by viewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()
    private lateinit var homePagerAdapter: HomePagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        glideRequests = GlideApp.with(this)
    }

    override fun initView() {
        binding.headerLayout.toolbar.setTitleDashboard(id = getString(R.string.title_home))
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
        observeNotNull(syncViewModel.syncState) { apiResponse ->
            apiResponse.whenLoading {
                binding.rootLayout.showHorizontalProgress(true)
//                requireContext().showSnackBar(
//                    view = binding.rootLayout,
//                    message = getString(R.string.msg_sync_started),
//                    duration = Snackbar.LENGTH_INDEFINITE,
//                    isError = false
//                )
            }
            apiResponse.whenInProgress {
                Log.d("it.completed.toDouble()", it.second.toDouble().toString())
                Log.d("it.total.toDouble()", it.first.toDouble().toString())
                if(it.first > 0) {
                    val progress =
                        it
                            .let { it.second.toDouble().div(it.first) }
                            .let { if (it.isNaN()) 0.0 else it }
                            .times(100)
                            .roundToInt()
                    "Synced $progress%".also { binding.rootLayout.showProgress(it)
                        Log.d("Synced", "$progress%")
                    }
                }else{
                    "Synced 0%".also { binding.rootLayout.showProgress(it) }
                }
            }
            apiResponse.handleListApiView(binding.rootLayout) {
                when(it) {
                    is SyncJobStatus.InProgress -> {
                        //Code to show text.
                        Log.d("it.completed.toDouble()", it.completed.toDouble().toString())
                        Log.d("it.total.toDouble()", it.total.toDouble().toString())
                        if(it.total > 0) {
                            val progress =
                                it
                                    .let { it.completed.toDouble().div(it.total) }
                                    .let { if (it.isNaN()) 0.0 else it }
                                    .times(100)
                                    .roundToInt()
                            "Synced $progress%".also { binding.rootLayout.showProgress(it) }
                            Log.d("Synced ", "$progress%")
                        }else{
                            "Synced 0%".also { binding.rootLayout.showProgress(it) }
                        }
                    }

                    is SyncJobStatus.Finished -> {
                        binding.rootLayout.updateProgressUi(true, true)
//                        requireContext().showSnackBar(
//                            view = binding.rootLayout,
//                            message = getString(R.string.msg_sync_successfully),
//                            duration = Snackbar.LENGTH_SHORT,
//                            isError = false
//                        )
                    }
                    is SyncJobStatus.Failed -> {
                        binding.rootLayout.showContent()
                        binding.rootLayout.updateProgressUi(true, false)
                        requireContext().showSnackBar(
                            view = binding.rootLayout,
                            message = getString(R.string.msg_sync_failed),
                            duration = Snackbar.LENGTH_SHORT,
                            isError = true
                        )
                    }
                }
            }
        }
    }
}