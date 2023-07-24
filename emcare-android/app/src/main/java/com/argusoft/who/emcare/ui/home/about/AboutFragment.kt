package com.argusoft.who.emcare.ui.home.about

import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentAboutBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AboutFragment : BaseFragment<FragmentAboutBinding>() {

    private val aboutViewModel: AboutViewModel by viewModels()

    override fun initView() {
        aboutViewModel.getBundleVersionNumber()
        binding.lastSyncTextView.text =
            preference.getLastSyncTimestamp().orEmpty { "Not yet Synced" }
        binding.headerLayout.toolbar.setTitleDashboard(id = getString(R.string.title_about))
    }

    override fun initListener() {
        initSyncAndMoreMenuItemListener(binding.headerLayout.toolbar)
    }

    override fun initObserver() {
        observeNotNull(aboutViewModel.bundleVersion) { apiResponse ->
            apiResponse.whenSuccess {
                if (it != null)
                    binding.bundleVersionTextView.text = it
            }
        }

        initObserverSync(binding.progressLayout, false)
        initObserverPurgeResources(binding.progressLayout, false)

    }
}