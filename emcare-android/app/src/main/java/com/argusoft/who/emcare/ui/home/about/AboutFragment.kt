package com.argusoft.who.emcare.ui.home.about

import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentAboutBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    private val syncViewModel: SyncViewModel by viewModels()
    private val aboutViewModel: AboutViewModel by viewModels()

    override fun initView() {
        aboutViewModel.getBundleVersionNumber()
        binding.lastSyncTextView.text = preference.getLastSyncTimestamp().orEmpty { "Not yet Synced" }
        binding.headerLayout.toolbar.setTitleDashboard(id = getString(R.string.title_about))
    }

    override fun initListener() {
        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sync -> {
                    syncViewModel.syncPatients()
                }
                R.id.action_more -> {
                    (activity as HomeActivity).openDrawer()
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun initObserver() {
        observeNotNull(aboutViewModel.bundleVersion) { apiResponse ->
            apiResponse.whenSuccess {
                if(it != null)
                    binding.bundleVersionTextView.text = it
            }
        }

        observeNotNull(syncViewModel.syncState) { apiResponse ->
            apiResponse.whenLoading {
                binding.progressLayout.showHorizontalProgress()
                requireContext().showSnackBar(
                    view = binding.progressLayout,
                    message = getString(R.string.msg_sync_started),
                    duration = Snackbar.LENGTH_INDEFINITE,
                    isError = false
                )
            }
            apiResponse.whenFailed {
                apiResponse.handleListApiView(binding.progressLayout) {
                    requireContext().showSnackBar(
                        view = binding.progressLayout,
                        message = getString(R.string.msg_sync_failed),
                        duration = Snackbar.LENGTH_SHORT,
                        isError = true
                    )
                }
            }
            apiResponse.whenSuccess {
                apiResponse.handleListApiView(binding.progressLayout) {
                    requireContext().showSnackBar(
                        view = binding.progressLayout,
                        message = getString(R.string.msg_sync_successfully),
                        duration = Snackbar.LENGTH_SHORT,
                        isError = false
                    )
                }
            }
        }
    }
}