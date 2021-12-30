package com.argusoft.who.emcare.ui.home.settings

import android.content.DialogInterface
import android.view.View
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentSettingsBinding
import com.argusoft.who.emcare.ui.common.base.BaseActivity
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.alertDialog
import com.argusoft.who.emcare.utils.localization.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    override fun initView() {
        binding.headerLayout.toolbar.setTitleAndBack(R.string.title_settings)
        binding.languageTextView.text = LocaleHelper.load(requireContext()).displayName.capitalize()
    }

    override fun initListener() {
        binding.languageTextView.setOnClickListener(this)
    }

    override fun initObserver() {
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.languageTextView -> {
                val list = arrayListOf("English", "हिन्दी")
                requireContext().alertDialog {
                    setTitle(R.string.label_select_lang)
                    setSingleChoiceItems(list.toTypedArray(), list.indexOf(binding.languageTextView.text), DialogInterface.OnClickListener { dialog, which ->
                        LocaleHelper.persist(
                            requireContext(), Locale(
                                when (which) {
                                    1 -> "hi"
                                    else -> "en"
                                }
                            )
                        )
                        (activity as? BaseActivity<*>)?.restartApp()
                    })
                }.show()
            }
        }
    }
}