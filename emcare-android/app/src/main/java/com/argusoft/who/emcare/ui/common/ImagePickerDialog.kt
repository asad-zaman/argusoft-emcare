package com.argusoft.who.emcare.ui.common

import android.os.Bundle
import android.view.View
import com.argusoft.who.emcare.databinding.DialogImagePickerBinding
import com.argusoft.who.emcare.ui.common.base.BaseBottomSheetDialogFragment
import com.argusoft.who.emcare.utils.common.ImagePickerUtils

/**
 * USAGE : To open Camera and Gallery file picker via BottomSheetDialog
 * Created by
 */
class ImagePickerDialog private constructor() : BaseBottomSheetDialogFragment<DialogImagePickerBinding>() {

    companion object {
        fun newInstance(
            optionSelector: ImagePickerUtils.OptionSelect,
            hasSelectMultiple: Boolean = false
        ): ImagePickerDialog {
            val frag = ImagePickerDialog()
            frag.setImageSelector(optionSelector, hasSelectMultiple)
            return frag
        }
    }

    private var optionSelector: ImagePickerUtils.OptionSelect? = null
    private var hasSelectMultiple: Boolean = false

    private fun setImageSelector(
        optionSelector: ImagePickerUtils.OptionSelect,
        hasSelectMultiple: Boolean
    ) {
        this.optionSelector = optionSelector
        this.hasSelectMultiple = hasSelectMultiple
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.tvCancel.setOnClickListener { dismiss() }
        binding.tvCamera.setOnClickListener {
            // Select image from camera
            ImagePickerUtils.clearAllContext()
            optionSelector?.cameraClick()
            dismiss()
        }
        binding.tvGallery.setOnClickListener {
            // Select image from gallery
            ImagePickerUtils.clearAllContext()
            optionSelector?.galleryClick()
            dismiss()
        }
    }

    override fun initView() {
        //Empty Block
    }

    override fun initListener() {
        //Empty Block
    }

    override fun initObserver() {
        //Empty Block
    }
}
