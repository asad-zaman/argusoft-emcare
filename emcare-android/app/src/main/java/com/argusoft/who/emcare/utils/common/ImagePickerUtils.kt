package com.argusoft.who.emcare.utils.common

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.TextUtils
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.utils.extention.*
import com.argusoft.who.emcare.utils.glide.GlideApp
import com.argusoft.who.emcare.widget.ApiViewStateConstraintLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.lang.ref.WeakReference
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object ImagePickerUtils : EasyPermissions.PermissionCallbacks {

    const val RC_CAMERA = 101
    const val RC_STORAGE = 102
    internal const val REQ_TAKE_PHOTO_URI = 500
    internal const val REQ_CHOOSE_GALLERY = 600
    private var weakReferenceActivity: WeakReference<Activity?>? = null
    private var weakReferenceFragment: WeakReference<Fragment?>? = null
    private var photoPath: String? = null
    private var imageSelector: ImageSelector? = null
    private var hasSelectMultiple: Boolean = false
    private var isAllowInExternal: Boolean = false
    private var listOfImages: MutableList<String> = ArrayList()
    private var rootLayout: ApiViewStateConstraintLayout? = null
    private var fragmentActivityLauncher: ActivityResultLauncher<Intent>? = null
    private fun getActivity() =
        weakReferenceActivity?.get() ?: weakReferenceFragment?.get()?.activity

    /**
     * Open camera task with permission
     */
    fun selectFromCamera(
        activity: Activity?,
        takePhotoLauncher: ActivityResultLauncher<Intent>,
        imageSelector: ImageSelector?,
        rootLayout: ApiViewStateConstraintLayout? = null,
        isAllowInExternal: Boolean = false
    ) {
        ImagePickerUtils.rootLayout = rootLayout
        ImagePickerUtils.imageSelector = imageSelector
        ImagePickerUtils.isAllowInExternal = isAllowInExternal
        fragmentActivityLauncher = takePhotoLauncher
        weakReferenceActivity = WeakReference(activity)
        openCamera()
    }

    fun selectFromCamera(
        fragment: Fragment?,
        takePhotoLauncher: ActivityResultLauncher<Intent>,
        imageSelector: ImageSelector?,
        rootLayout: ApiViewStateConstraintLayout? = null,
        isAllowInExternal: Boolean = false
    ) {
        ImagePickerUtils.rootLayout = rootLayout
        ImagePickerUtils.imageSelector = imageSelector
        ImagePickerUtils.isAllowInExternal = isAllowInExternal
        fragmentActivityLauncher = takePhotoLauncher
        weakReferenceFragment = WeakReference(fragment)
        openCamera()
    }

    @AfterPermissionGranted(RC_CAMERA)
    fun openCamera() {
        if (hasCameraAndStoragePermission() == true) {
            dispatchTakePictureIntent() // Uri permission required
        } else {
            // Request one permission
            weakReferenceActivity?.get()?.let { activity ->
                EasyPermissions.requestPermissions(
                    activity,
                    activity.getString(R.string.rationale_camera),
                    RC_CAMERA,
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            weakReferenceFragment?.get()?.let { fragment ->
                EasyPermissions.requestPermissions(
                    fragment,
                    fragment.getString(R.string.rationale_camera),
                    RC_CAMERA,
                    Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    /**
     * Open camera intent
     */
    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (getActivity()?.packageManager?.let {
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).resolveActivity(
                    it
                )
            } != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                ex.printStackTrace()
                // Error occurred while creating the File
            }
            photoFile?.let {
                takePictureIntent.putExtra(
                    MediaStore.EXTRA_OUTPUT,
                    getActivity()?.getUri(it)
                ) // Set output uri
            }
            photoPath = photoFile?.absolutePath
        }
        fragmentActivityLauncher?.launch(takePictureIntent)
    }

    /**
     * Create temp image file to store captured image
     * NOTE:Only required when need to process captured photo
     */
    @Throws(IOException::class)
    private fun createImageFile(): File? {
        //  Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "${getActivity()?.getString(R.string.app_name)}_IMG_$timeStamp.jpg"
        return getActivity()?.createFile(
            getActivity()?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                .toString() + File.separator + imageFileName
        )
    }

    /**
     * Storage specific permission check
     */
    private fun hasStoragePermission(): Boolean? {
        return getActivity()?.let {
            EasyPermissions.hasPermissions(
                it,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    /**
     * Camera specific permission check
     */
    private fun hasCameraAndStoragePermission(): Boolean? {
        return getActivity()?.let {
            EasyPermissions.hasPermissions(
                it,
                Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (getActivity()?.let {
                EasyPermissions.somePermissionPermanentlyDenied(
                    it,
                    perms
                )
            } == true) {
            AppSettingsDialog.Builder(getActivity()!!).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        //Empty Block
    }


    /**
     * Open Gallery task with permission
     */
    fun selectFromGallery(
        activity: Activity?,
        choosePhotoLauncher: ActivityResultLauncher<Intent>,
        imageSelector: ImageSelector?,
        rootLayout: ApiViewStateConstraintLayout? = null,
        hasSelectMultiple: Boolean = false
    ) {
        weakReferenceActivity = weakReferenceActivity ?: WeakReference(activity)
        ImagePickerUtils.rootLayout = rootLayout
        ImagePickerUtils.imageSelector = imageSelector
        ImagePickerUtils.hasSelectMultiple = hasSelectMultiple
        fragmentActivityLauncher = choosePhotoLauncher
        openGallery()
    }

    fun selectFromGallery(
        fragment: Fragment?,
        choosePhotoLauncher: ActivityResultLauncher<Intent>,
        imageSelector: ImageSelector?,
        rootLayout: ApiViewStateConstraintLayout? = null,
        hasSelectMultiple: Boolean = false
    ) {
        weakReferenceFragment = weakReferenceFragment ?: WeakReference(fragment)
        ImagePickerUtils.rootLayout = rootLayout
        ImagePickerUtils.imageSelector = imageSelector
        ImagePickerUtils.hasSelectMultiple = hasSelectMultiple
        fragmentActivityLauncher = choosePhotoLauncher
        openGallery()
    }

    @AfterPermissionGranted(RC_STORAGE)
    fun openGallery() {
        if (hasStoragePermission() == true) {
            // Have permission, do the thing!
            dispatchGalleryIntent()
        } else {
            // Request one permission
            weakReferenceActivity?.get()?.let { activity ->
                EasyPermissions.requestPermissions(
                    activity,
                    activity.getString(R.string.rationale_gallery),
                    RC_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
            weakReferenceFragment?.get()?.let { fragment ->
                EasyPermissions.requestPermissions(
                    fragment,
                    fragment.getString(R.string.rationale_gallery),
                    RC_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
//            weakReferenceActivity?.clear()
//            weakReferenceActivity = null
        }
    }

    /**
     * Open gallery intent
     */
    private fun dispatchGalleryIntent() {
        try {
            val openGalleryIntent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                addCategory(Intent.CATEGORY_OPENABLE)
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, hasSelectMultiple)
            }
            fragmentActivityLauncher?.launch(openGalleryIntent)
        } catch (e: ActivityNotFoundException) {
            getActivity().showToast(message = "No any apps found to open.")
        }
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQ_TAKE_PHOTO_URI) {
                if (isAllowInExternal)
                    addToGallery()
                else
                    saveBitmapOnCatch(getActivity()?.getUri(File(photoPath)))
            } else if (requestCode == REQ_CHOOSE_GALLERY && data != null) {
                if (hasSelectMultiple) {
                    val clipData = data.clipData
                    if (clipData != null) {
                        listOfImages.clear()
                        for (i in 0 until clipData.itemCount) {
                            listOfImages.add(clipData.getItemAt(i).uri.toString())
                        }
                        setImageSelected()
                    }
                } else {
                    val selectedImageUri = data.data
                    if (selectedImageUri != null) {
                        saveBitmapOnCatch(selectedImageUri)
                    }
                }
            }
        } else {
            clearAllContext()
        }
    }

    private fun saveBitmapOnCatch(imageUri: Uri?) {
        imageSelector?.onShowProgress()
        GlobalScope.launch(Dispatchers.Main) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                withContext(Dispatchers.IO) {
                    val bitmap =
                        getActivity()?.contentResolver?.let {
                            imageUri?.let { it1 ->
                                ImageDecoder.createSource(
                                    it,
                                    it1
                                )
                            }
                        }
                    bitmap?.let { ImageDecoder.decodeBitmap(it) }?.let { saveFile(it) }
                }

            } else {
                withContext(Dispatchers.IO) {
                    getActivity()?.let {
                        val bitmap = GlideApp.with(it).asBitmap().load(imageUri).submit().get()
                        saveFile(bitmap)
                    }
                }
            }
            imageSelector?.onHideProgress()
            clearAllContext()
        }

    }

    fun clearAllContext() {
        weakReferenceActivity?.clear()
        weakReferenceActivity = null
        weakReferenceFragment?.clear()
        weakReferenceFragment = null
    }

    private fun saveFile(bitmap: Bitmap) {
        photoPath = getActivity()?.createFileInCacheFromBitmap(
            bitmap = bitmap,
            fileName = "${System.currentTimeMillis()}.jpg"
        )?.absolutePath
        GlobalScope.launch(Dispatchers.Main) {
            setImageSelected()
            rootLayout?.showContent()
        }
    }

    private fun addToGallery() {
        val contentResolver = getActivity()?.contentResolver
        val file = photoPath?.let { File(it) }
        val source = file?.createBitmapFromFile()
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, file?.nameWithoutExtension)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, file?.nameWithoutExtension)
        values.put(MediaStore.Images.Media.DESCRIPTION, file?.nameWithoutExtension)
        values.put(MediaStore.Images.Media.MIME_TYPE, file?.getMimeType())
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            /*values.put(
                MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}${File.separator}" +
                        "${getActivity()?.getString(R.string.app_name)}"
            )*/
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
        }
        var url: Uri? = null
        try {
            url = contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            val imageOut = url?.let { contentResolver?.openOutputStream(it) }
            imageOut.use { imageOut ->
                source?.compress(Bitmap.CompressFormat.JPEG, 100, imageOut)
            }
        } catch (e: Exception) {
            if (url != null) {
                contentResolver?.delete(url, null, null)
                url = null
            }
        }
        if (url != null) {
            file?.delete()
            photoPath = url.toString()
        }
    }

    /**
     * Perform image selection task
     */
    private fun setImageSelected() {
        //send the image selected without image compression
        imageSelector?.also {
            if (hasSelectMultiple) {
                imageSelector?.onMultipleImageSelected(listOfImages)
            } else {
                if (!TextUtils.isEmpty(photoPath)) {
                    imageSelector?.onImageSelected(photoPath!!)
                } else {
                    getActivity()?.let {
                        it.showSnackBar(
                            it.findViewById(android.R.id.content),
                            it.getString(R.string.alert_error_selecting_image)
                        )
                    }
                }
            }
        }
    }

    interface ImageSelector {
        fun onImageSelected(path: String)

        fun onShowProgress()

        fun onHideProgress()

        fun onMultipleImageSelected(paths: List<String>)
    }

    interface OptionSelect {
        fun cameraClick()
        fun galleryClick()
    }

}