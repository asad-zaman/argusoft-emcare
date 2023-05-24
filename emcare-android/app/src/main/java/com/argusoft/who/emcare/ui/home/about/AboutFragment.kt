package com.argusoft.who.emcare.ui.home.about

import android.util.Log
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentAboutBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.auth.login.LoginViewModel
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    private val syncViewModel: SyncViewModel by viewModels()
    private val aboutViewModel: AboutViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun initView() {
        aboutViewModel.getBundleVersionNumber()
        binding.lastSyncTextView.text =
            preference.getLastSyncTimestamp().orEmpty { "Not yet Synced" }
        binding.headerLayout.toolbar.setTitleDashboard(id = getString(R.string.title_about))
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
    }

    override fun initObserver() {
        observeNotNull(aboutViewModel.bundleVersion) { apiResponse ->
            apiResponse.whenSuccess {
                if (it != null)
                    binding.bundleVersionTextView.text = it
            }
        }
        observeNotNull(syncViewModel.syncState) { apiResponse ->
            apiResponse.whenLoading {
                binding.progressLayout.showHorizontalProgress(true)
//                requireContext().showSnackBar(
//                    view = binding.progressLayout,
//                    message = getString(R.string.msg_sync_started),
//                    duration = Snackbar.LENGTH_INDEFINITE,
//                    isError = false
//                )
            }
            apiResponse.whenInProgress {
                Log.d("it.total.toDouble()", it.first.toDouble().toString())
                Log.d("it.progress", it.second.toDouble().toString())
                if(it.second == 100){
                    binding.progressLayout.updateProgressUi(true, true)
                    loginViewModel.addDevice(
                        getDeviceName(),
                        getDeviceOS(),
                        getDeviceModel(),
                        requireContext().getDeviceUUID().toString(),
                        BuildConfig.VERSION_NAME
                    )
                }else if (it.first > 0) {
                    val progress = it.second
                    "Synced $progress%".also {
                        binding.progressLayout.showProgress(it)
                        Log.d("Synced", "$progress%")
                    }
                }else if(it.first == 0){
                    binding.progressLayout.updateProgressUi(true, true)
                }
            }

            apiResponse.handleListApiView(binding.progressLayout) {
                when (it) {

//                    is SyncJobStatus.Finished -> {
//                        binding.progressLayout.updateProgressUi(true, true)
//                        loginViewModel.addDevice(
//                            getDeviceName(),
//                            getDeviceOS(),
//                            getDeviceModel(),
//                            requireContext().getDeviceUUID().toString(),
//                            BuildConfig.VERSION_NAME
//                        )
//                    }
                    is SyncJobStatus.Failed -> {
                        binding.progressLayout.showContent()
                        binding.progressLayout.hideProgressUi()
//                        binding.progressLayout.updateProgressUi(true, false)
                        requireContext().showSnackBar(
                            view = binding.progressLayout,
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