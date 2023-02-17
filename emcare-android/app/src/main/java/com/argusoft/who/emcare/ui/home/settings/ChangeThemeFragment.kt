package com.argusoft.who.emcare.ui.home.settings

import android.content.Intent
import android.view.View
import android.widget.CompoundButton
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentScreenResizeSettingsBinding
import com.argusoft.who.emcare.sync.SyncState
import com.argusoft.who.emcare.sync.SyncViewModel
import com.argusoft.who.emcare.ui.common.APP_THEME_COMFORTABLE
import com.argusoft.who.emcare.ui.common.APP_THEME_COMPACT
import com.argusoft.who.emcare.ui.common.APP_THEME_ENLARGED
import com.argusoft.who.emcare.ui.common.base.BaseActivity
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.*
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ChangeThemeFragment : BaseFragment<FragmentScreenResizeSettingsBinding>() {

    private val syncViewModel: SyncViewModel by viewModels()

    override fun initView() {
        binding.headerLayout.toolbar.setTitleDashboard(id = getString(R.string.title_change_theme))
        when (preference.getTheme()){
            APP_THEME_COMPACT ->
                binding.radioButton1.isChecked = true
            APP_THEME_COMFORTABLE ->
                binding.radioButton2.isChecked = true
            APP_THEME_ENLARGED ->
                binding.radioButton3.isChecked = true

        }
        RadioGroupCheckListener.makeGroup(binding.radioButton1, binding.radioButton2, binding.radioButton3)
    }

    override fun initListener() {
        binding.headerLayout.toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.action_sync -> {
                    syncViewModel.syncPatients()
                }
                R.id.action_more -> {
                    (activity as HomeActivity).openDrawer()
                }
            }
            return@setOnMenuItemClickListener true
        }
        binding.saveButton.setOnClickListener(this)
        binding.closeButton.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.save_button -> {
                if(binding.radioButton1.isChecked){
                    preference.setTheme(APP_THEME_COMPACT)
                }else if(binding.radioButton2.isChecked){
                    preference.setTheme(APP_THEME_COMFORTABLE)
                }else if(binding.radioButton3.isChecked){
                    preference.setTheme(APP_THEME_ENLARGED)
                }
                startActivity(Intent(requireContext(), HomeActivity::class.java))
                requireActivity().finish()
            }
            R.id.close_button -> {
                requireActivity().onBackPressed()
            }
        }
    }

    override fun initObserver() {
        observeNotNull(syncViewModel.syncState) { apiResponse ->
            apiResponse.whenLoading {
                binding.rootLayout.showHorizontalProgress()
                requireContext().showSnackBar(
                    view = binding.rootLayout,
                    message = getString(R.string.msg_sync_started),
                    duration = Snackbar.LENGTH_INDEFINITE,
                    isError = false
                )
            }
            apiResponse.handleListApiView(binding.rootLayout) {
                when (it) {
                    is SyncState.Finished -> {
                        requireContext().showSnackBar(
                            view = binding.rootLayout,
                            message = getString(R.string.msg_sync_successfully),
                            duration = Snackbar.LENGTH_SHORT,
                            isError = false
                        )
                    }
                    is SyncState.Failed -> {
                        binding.rootLayout.showContent()
                        requireContext().showSnackBar(
                            view = binding.rootLayout,
                            message = getString(R.string.msg_sync_failed),
                            duration = Snackbar.LENGTH_SHORT,
                            isError = true
                        )
                    }
                }
            }
        }
    }

    class RadioGroupCheckListener(vararg allies: Array<CompoundButton?>) :
        CompoundButton.OnCheckedChangeListener {
        private val allies: Array<CompoundButton?>

        /**
         * listener for a button to turn other allies unchecked when it turn checked
         * @param buttonView change check occur with this button
         * @param isChecked result of changing
         */
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) {
                for (aly in allies) {
                    aly?.isChecked = false
                }
            }
        }

        companion object {
            /**
             * inner private function to remove one element from Buttons array
             * @param buttons all the buttons in RadioGroup
             * @param me the index that we want to remove from array
             * @return an array of CompoundButtons except the index of me
             */
            private fun exceptMe(buttons: Array<CompoundButton>, me: Int): Array<CompoundButton?> {
                val result = arrayOfNulls<CompoundButton>(buttons.size - 1)
                var i = 0
                var j = 0
                while (i < buttons.size) {
                    if (i == me) {
                        i++
                        continue
                    }
                    result[j] = buttons[i]
                    j++
                    i++
                }
                return result
            }

            /**
             * static function to create a RadioGroup
             * if a button turn to checked state all other buttons is group turn unchecked
             * @param buttons the buttons that we want to act like RadioGroup
             */
            fun makeGroup(vararg buttons: CompoundButton) {
                for (i in 0 until buttons.size) {
                    buttons[i].setOnCheckedChangeListener(
                        RadioGroupCheckListener(
                            *arrayOf(
                                exceptMe(
                                    buttons as Array<CompoundButton>,
                                    i
                                )
                            )
                        )
                    )
                }
            }
        }

        /**
         * public generator - indicate allies
         * @param allies all other buttons in the same RadioGroup
         */
        init {
            this.allies = allies.flatten().toTypedArray()
        }
    }
}