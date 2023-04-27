package com.argusoft.who.emcare.ui.auth.login

import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentLoginBinding
import com.argusoft.who.emcare.sync.SyncState
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.REQUEST_CODE_READ_PHONE_STATE
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import kotlin.math.roundToInt

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(), EasyPermissions.PermissionCallbacks {

    private val syncViewModel: SyncViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val formatString12 = "dd/MM/yyyy hh:mm:ss a"

    override fun initView() {
        if(preference.getCountry().isNotBlank()){
            binding.emcareTitleTextView.text = binding.emcareTitleTextView.text.toString() + " " + preference.getCountry()
        }
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
                binding.progressLayout.updateProgressUi(true, false)
                syncViewModel.syncPatients(true)
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
                Log.d("it.completed.toDouble()", it.second.toDouble().toString())
                Log.d("it.total.toDouble()", it.first.toDouble().toString())
                if(it.first.toDouble() == it.second.toDouble()){
                    loginViewModel.addDevice(
                        getDeviceName(),
                        getDeviceOS(),
                        getDeviceModel(),
                        requireContext().getDeviceUUID().toString(),
                        BuildConfig.VERSION_NAME
                    )
                    startActivity(Intent(requireContext(), HomeActivity::class.java))
                    requireActivity().finish()
                }else if(it.first > 0) {
                    val progress =
                        it
                            .let { it.second.toDouble().div(it.first) }
                            .let { if (it.isNaN()) 0.0 else it }
                            .times(100)
                            .roundToInt()
                    "Synced $progress%".also { binding.progressLayout.showProgress(it)
                        Log.d("Synced", "$progress%")
                    }
                }else{
                    "Synced 0%".also { binding.progressLayout.showProgress(it) }
                }
            }

            apiResponse.handleListApiView(binding.progressLayout) {
                when (it) {
//                    is SyncJobStatus.InProgress -> {
//                        Log.d("it.completed.toDouble()", it.completed.toDouble().toString())
//                        Log.d("it.total.toDouble()", it.total.toDouble().toString())
//                        if(it.total > 0) {
//                            val progress =
//                                it
//                                    .let { it.completed.toDouble().div(it.total) }
//                                    .let { if (it.isNaN()) 0.0 else it }
//                                    .times(100)
//                                    .roundToInt()
//                            "Synced $progress%".also { binding.progressLayout.showProgress(it)
//                                Log.d("Synced", "$progress%")
//                            }
//                        }else{
//                            "Synced 0%".also { binding.progressLayout.showProgress(it) }
//                        }
//                        //Code to show text.
//                        //Reference: https://github.com/google/android-fhir/blob/master/demo/src/main/java/com/google/android/fhir/demo/PatientListFragment.kt
//                    }

                    is SyncJobStatus.Finished -> {
                        binding.progressLayout.updateProgressUi(true, true)
//                        requireContext().showSnackBar(
//                            view = binding.progressLayout,
//                            message = getString(R.string.msg_sync_successfully),
//                            duration = Snackbar.LENGTH_SHORT,
//                            isError = false
//                        )
                        loginViewModel.addDevice(
                            getDeviceName(),
                            getDeviceOS(),
                            getDeviceModel(),
                            requireContext().getDeviceUUID().toString(),
                            BuildConfig.VERSION_NAME
                        )
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    }
                    is SyncJobStatus.Failed -> {
                        binding.progressLayout.showContent()
                        binding.progressLayout.updateProgressUi(true, false)
                        requireContext().showSnackBar(
                            view = binding.progressLayout,
                            message = getString(R.string.msg_sync_failed),
                            duration = Snackbar.LENGTH_SHORT,
                            isError = true
                        )
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    }
                }
            }
        }
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