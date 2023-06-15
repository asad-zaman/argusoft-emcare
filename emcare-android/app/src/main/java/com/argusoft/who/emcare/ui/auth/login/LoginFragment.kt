package com.argusoft.who.emcare.ui.auth.login

import android.Manifest
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentLoginBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.REQUEST_CODE_READ_PHONE_STATE
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.getDeviceModel
import com.argusoft.who.emcare.utils.extention.getDeviceName
import com.argusoft.who.emcare.utils.extention.getDeviceOS
import com.argusoft.who.emcare.utils.extention.getDeviceUUID
import com.argusoft.who.emcare.utils.extention.getEnterText
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.showSnackBar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(), EasyPermissions.PermissionCallbacks {

    private val syncViewModel: SyncViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun initView() {
        if (preference.getCountry().isNotBlank()) {
            binding.emcareTitleTextView.text =
                binding.emcareTitleTextView.text.toString() + " " + preference.getCountry()
        }
//        loginViewModel.clearData()
    }

    override fun initListener() {
        binding.loginButton.setOnClickListener(this)
        binding.signupTextView.setOnClickListener(this)
    }

    override fun initObserver() {
        observeNotNull(loginViewModel.errorMessageState) {
            binding.progressLayout.showContent()
            binding.progressLayout.updateProgressUi(true, false)
            context?.showSnackBar(
                view = binding.progressLayout,
                message = getString(it),
                isError = true
            )
        }

        observeNotNull(loginViewModel.loginApiState) {
            it.handleApiView(binding.progressLayout) {
                if (preference.getFacilityId().isNotEmpty()) {
                    binding.progressLayout.updateProgressUi(true, false)
                    Log.d("Sync Called", "Above SyncPatients")
                    syncViewModel.syncPatients(true)
                }

            }
        }

        initObserverSync(binding.progressLayout, true)
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.loginButton -> {
                preference.setSelectedCountry("")
                deviceInfo()
            }

            R.id.signupTextView -> {
                navigate(R.id.action_loginFragment_to_signUpFragment)
            }
        }
    }

    private fun deviceInfo() {
        if (hasReadPhoneStatePermission()) {
            loginViewModel.login(
                binding.loginIdEditText.editText?.getEnterText()!!,
                binding.passwordEditText.editText?.getEnterText()!!,
                getDeviceName(),
                getDeviceOS(),
                getDeviceModel(),
                requireContext().getDeviceUUID().toString(),
                BuildConfig.VERSION_NAME
            )
        } else {
            EasyPermissions.requestPermissions(
                this,
                getString(R.string.rationale_phone_state),
                REQUEST_CODE_READ_PHONE_STATE,
                Manifest.permission.READ_PHONE_STATE,
            )
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        deviceInfo()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun hasReadPhoneStatePermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.READ_PHONE_STATE
        )
    }
}