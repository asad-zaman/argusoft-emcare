package com.argusoft.who.emcare.ui.auth.signup

import android.view.View
import android.widget.ArrayAdapter
import androidx.core.view.get
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentSignupBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.navigate

class SignUpFragment : BaseFragment<FragmentSignupBinding>() {

    override fun initView() {
        setupLocationAutoComplete()
        setupRoleAutoComplete()
    }

    private fun setupLocationAutoComplete() {
        binding.locationEditText.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.select_dialog_item,
                arrayOf("India", "USA", "Singapore", "UK", "UAE")
            )
        )
    }

    private fun setupRoleAutoComplete() {
        binding.roleEditText.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.select_dialog_item,
                arrayOf("Front Line", "Medical Servant")
            )
        )
    }

    override fun initListener() {
        binding.nextButton.setOnClickListener(this)
        binding.submitButton.setOnClickListener(this)
        binding.headerLayout.toolbar.setNavigationOnClickListener(this)
    }

    override fun initObserver() {
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.nextButton -> {
                binding.viewSwitcher.showNext()
            }
            R.id.submitButton->{
                navigate(R.id.action_signUpFragment_to_successFragment)
            }
            else -> {
                if (binding.viewSwitcher.currentView.id != R.id.firstView)
                    binding.viewSwitcher.showPrevious()
                else
                    requireActivity().onBackPressed()
            }
        }
    }
}