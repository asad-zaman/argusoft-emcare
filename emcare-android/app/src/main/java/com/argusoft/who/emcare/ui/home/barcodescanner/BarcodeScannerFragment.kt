package com.argusoft.who.emcare.ui.home.barcodescanner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.ViewGroup
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.argusoft.who.emcare.BuildConfig
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentBarcodeScannerBinding
import com.argusoft.who.emcare.ui.common.QrCodeAnalyzer
import com.argusoft.who.emcare.ui.common.REQUEST_CODE_READ_PHONE_STATE
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BarcodeScannerFragment : BaseFragment<FragmentBarcodeScannerBinding>(), EasyPermissions.PermissionCallbacks{


    private lateinit var cameraExecutor: ExecutorService
    private lateinit var barcodeBoxView: BarcodeBoxView

    override fun initView() {
        cameraExecutor = Executors.newSingleThreadExecutor()
        barcodeBoxView = BarcodeBoxView(requireContext())
//        binding.previewView.addView(barcodeBoxView, ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT))
        startCamera()

    //        if (hasCameraPermission()) {
//            startCamera()
//        } else {
//            EasyPermissions.requestPermissions(
//                this,
//                getString(R.string.rationale_phone_state),
//                REQUEST_CODE_READ_PHONE_STATE,
//                Manifest.permission.READ_PHONE_STATE,
//            )
//        }
    }

    override fun initListener() {
        //
    }

    override fun initObserver() {
        //
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    /**
     * This function is executed once the user has granted or denied the missing permission
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        startCamera()
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(requireActivity(), perms)) {
            AppSettingsDialog.Builder(requireActivity()).build().show()
        }
    }

    /**
     * This function is responsible for the setup of the camera preview and the image analyzer.
     */
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.previewView.surfaceProvider)
                }

            // Image analyzer
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(
                        cameraExecutor,
                        QrCodeAnalyzer(
                            requireContext(),
                            barcodeBoxView,
                            binding.previewView.width.toFloat(),
                            binding.previewView.height.toFloat()
                        )
                    )
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun hasCameraPermission(): Boolean {
        return EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.CAMERA
        )
    }
}