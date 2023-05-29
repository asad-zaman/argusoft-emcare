package com.argusoft.who.emcare.ui.home.about

import android.os.Handler
import android.os.Looper
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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
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

        initObserverSync(binding.progressLayout, false)
    }
}