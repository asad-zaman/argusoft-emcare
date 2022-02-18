package com.argusoft.who.emcare.ui.home.settings

import android.content.DialogInterface
import android.view.View
import androidx.fragment.app.activityViewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentSettingsBinding
import com.argusoft.who.emcare.ui.common.base.BaseActivity
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.Language
import com.argusoft.who.emcare.utils.extention.*
import com.argusoft.who.emcare.utils.localization.LocaleHelper
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class SettingsFragment : BaseFragment<FragmentSettingsBinding>() {

    private val settingsViewModel: SettingsViewModel by activityViewModels()
    private var languageList: List<Language>? = null

    override fun initView() {
        binding.languageTextView.text = LocaleHelper.load(requireContext()).displayName.capitalize()
    }

    override fun initListener() {
        binding.languageTextView.setOnClickListener(this)
    }

    override fun initObserver() {
        observeNotNull(settingsViewModel.languagesApiState) { apiResponse ->
            apiResponse.handleApiView(binding.progressLayout) {
                languageList = it
            }
        }
        observeNotNull(settingsViewModel.languageApiState) {
            it.whenSuccess {
                it.languageData?.convertToMap()?.apply {
                    binding.languageLabelTextView.text = getOrElse("Language") { getString(R.string.label_language) }
                    binding.languageTextView.tag = getOrElse("Select_Language") { getString(R.string.label_select_lang) }
                    binding.headerLayout.toolbar.setTitleAndBack(getOrElse("Settings") { getString(R.string.title_settings) })
                }
            }
        }
    }

    override fun onClick(view: View?) {
        val languageNames = languageList?.map { it.languageName }
        val languageCodes = languageList?.map { it.languageCode }
        super.onClick(view)
        when (view?.id) {
            R.id.languageTextView -> {
                requireContext().alertDialog {
                    setTitle(view.tag as? String)
                    if (languageNames != null) {
                        val selectedLanguage = languageList?.indexOf(languageList?.find { it.languageCode == LocaleHelper.load(requireContext()).language }) ?: -1
                        setSingleChoiceItems(languageNames?.toTypedArray(), selectedLanguage, DialogInterface.OnClickListener { dialog, which ->
                            if (languageCodes != null) {
                                LocaleHelper.persist(
                                    requireContext(), Locale(
                                        languageCodes?.getOrNull(which)
                                    )
                                )
                            }
                            dialog.dismiss()
                            (activity as? BaseActivity<*>)?.restartApp()
                        })
                    }
                }.show()
            }
        }
    }
}