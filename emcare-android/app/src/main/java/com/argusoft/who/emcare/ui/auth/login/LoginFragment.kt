package com.argusoft.who.emcare.ui.auth.login

import android.Manifest
import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentLoginBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.REQUEST_CODE_READ_PHONE_STATE
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.sync.State
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(), EasyPermissions.PermissionCallbacks {

    private val syncViewModel: SyncViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()

    override fun initView() {
    }

    override fun initListener() {
        binding.loginButton.setOnClickListener(this)
        binding.signupTextView.setOnClickListener(this)
    }

    override fun initObserver() {
        observeNotNull(loginViewModel.errorMessageState) {
            binding.progressLayout.showContent()
            context?.showSnackBar(
                view = binding.progressLayout,
                message = getString(it),
                isError = true
            )
        }
        observeNotNull(loginViewModel.loginApiState) {
            it.handleApiView(binding.progressLayout) {
                syncViewModel.syncPatients()

            }
        }

        observeNotNull(syncViewModel.syncState) {
            when (it) {
                is State.Started -> requireContext().showToast(messageResId = R.string.msg_sync_started)
                is State.Finished -> {
                    requireContext().showToast(messageResId = R.string.msg_sync_successfully)
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                    requireActivity().finish()
                }
                is State.Failed -> {
                    requireContext().showToast(messageResId = R.string.msg_sync_failed)
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                    requireActivity().finish()
                }
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.loginButton -> {
              deviceInfo()
            }
            R.id.signupTextView -> {
                navigate(R.id.action_loginFragment_to_signUpFragment)
            }
        }
    }

    @AfterPermissionGranted(REQUEST_CODE_READ_PHONE_STATE)
    fun deviceInfo() {
        if (hasReadPhoneStatePermission()) {
            loginViewModel.login(
                binding.usernameEditText.getEnterText(),
                binding.passwordEditText.getEnterText(),
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
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