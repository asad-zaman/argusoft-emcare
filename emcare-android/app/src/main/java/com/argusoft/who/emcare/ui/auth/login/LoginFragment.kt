package com.argusoft.who.emcare.ui.auth.login

import android.Manifest
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentLoginBinding
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.REQUEST_CODE_READ_PHONE_STATE
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.getDeviceModel
import com.argusoft.who.emcare.utils.extention.getDeviceName
import com.argusoft.who.emcare.utils.extention.getDeviceOS
import com.argusoft.who.emcare.utils.extention.getDeviceUUID
import com.argusoft.who.emcare.utils.extention.getEnterText
import com.argusoft.who.emcare.utils.extention.handleApiView
import com.argusoft.who.emcare.utils.extention.handleListApiView
import com.argusoft.who.emcare.utils.extention.navigate
import com.argusoft.who.emcare.utils.extention.observeNotNull
import com.argusoft.who.emcare.utils.extention.showSnackBar
import com.argusoft.who.emcare.utils.extention.whenInProgress
import com.argusoft.who.emcare.utils.extention.whenLoading
import com.google.android.fhir.sync.SyncJobStatus
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.Timer
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>(), EasyPermissions.PermissionCallbacks {

    private val syncViewModel: SyncViewModel by viewModels()
    private val loginViewModel: LoginViewModel by viewModels()
    private val formatString12 = "dd/MM/yyyy hh:mm:ss a"
    private var progressCount : Int = 0

    override fun initView() {
        if(preference.getCountry().isNotBlank()){
            binding.emcareTitleTextView.text = binding.emcareTitleTextView.text.toString() + " " + preference.getCountry()
        }
        loginViewModel.clearData()
    }

    fun clearApplicationData() {
        val cache: File = requireContext().getCacheDir()
        val appDir = File(cache.getParent())
        if (appDir.exists()) {
            val children: Array<String> = appDir.list()
            for (s in children) {
                if (s != "lib") {
                    deleteDir(File(appDir, s))
                    Log.i("TAG", "File /data/data/APP_PACKAGE/$s DELETED")
                }
            }
        }
    }

    private fun deleteDir(dir: File?): Boolean {
        if (dir != null && dir.isDirectory) {
            val children = dir.list()
            for (i in children.indices) {
                val success = deleteDir(File(dir, children[i]))
                if (!success) {
                    return false
                }
            }
        }
        return dir!!.delete()
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
                if(preference.getFacilityId().isNotEmpty()){
                    binding.progressLayout.updateProgressUi(true, false)
                    Log.d("Sync Called","Above SyncPatients")
                    syncViewModel.syncPatients(true)
                }

            }
        }

        observeNotNull(syncViewModel.syncState) { apiResponse ->

            apiResponse.whenLoading {
                if(preference.getFacilityId().isNotEmpty())
                    binding.progressLayout.showHorizontalProgress(true)
            }

            apiResponse.whenInProgress {
                Log.d("it.total.toDouble()", it.first.toDouble().toString())
                Log.d("it.progress.toDouble()", it.second.toDouble().toString())
                if(it.second >= 100){
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.progressLayout.updateProgressUi(true, true)
                        loginViewModel.addDevice(
                            getDeviceName(),
                            getDeviceOS(),
                            getDeviceModel(),
                            requireContext().getDeviceUUID().toString(),
                            BuildConfig.VERSION_NAME
                        )
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    }, 5000)
//                    Executors.newSingleThreadScheduledExecutor().schedule({
//
//                    }, 5, TimeUnit.SECONDS)
                }else if(it.first > 0 && it.second <=100) {
                    val progress = it.second
                    "Synced $progress%".also { binding.progressLayout.showProgress(it)
                        Log.d("Synced", "$progress%")
                    }
                }else if(it.first == 0){
                    if(preference.getFacilityId().isNotEmpty()) {
                        binding.progressLayout.updateProgressUi(true, true)
                        startActivity(Intent(requireContext(), HomeActivity::class.java))
                        requireActivity().finish()
                    }else{
                        binding.progressLayout.hideProgressUi()
                    }
                }
            }

            apiResponse.handleListApiView(binding.progressLayout) {
                when (it) {

//                    is SyncJobStatus.Finished -> {
//                        binding.progressLayout.updateProgressUi(true, true)
////                        requireContext().showSnackBar(
////                            view = binding.progressLayout,
////                            message = getString(R.string.msg_sync_successfully),
////                            duration = Snackbar.LENGTH_SHORT,
////                            isError = false
////                        )
//                        loginViewModel.addDevice(
//                            getDeviceName(),
//                            getDeviceOS(),
//                            getDeviceModel(),
//                            requireContext().getDeviceUUID().toString(),
//                            BuildConfig.VERSION_NAME
//                        )
//                        startActivity(Intent(requireContext(), HomeActivity::class.java))
//                        requireActivity().finish()
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