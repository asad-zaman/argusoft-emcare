package com.argusoft.who.emcare.ui.auth.signup

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentSignupBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.utils.extention.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignupBinding>(), AdapterView.OnItemSelectedListener {

    private val signUpViewModel: SignUpViewModel by viewModels()

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
        binding.locationEditText.onItemSelectedListener = this
        binding.roleEditText.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (view?.id) {
            R.id.locationEditText -> {
                "locationEditText $position".timber()
            }
            R.id.roleEditText -> {
                "roleEditText $position".timber()
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
    }

    override fun initObserver() {
        observeNotNull(signUpViewModel.errorMessageState) {
            if (it == 0) {
                binding.viewSwitcher.showNext()
            } else
                context?.showSnackBar(
                    view = binding.progressLayout,
                    message = getString(it),
                    isError = true
                )
        }
        observeNotNull(signUpViewModel.signupApiState) {
            it.handleApiView(binding.progressLayout) {
                navigate(R.id.action_signUpFragment_to_successFragment)
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.nextButton -> {
                signUpViewModel.validateSignup(
                    binding.firstNameEditText.getEnterText(),
                    binding.lastNameEditText.getEnterText(),
                    binding.emailEditText.getEnterText(),
                    0,
                    0
                )
            }
            R.id.submitButton -> {
                signUpViewModel.signup(
                    binding.passwordEditText.getEnterText(),
                    binding.confirmPasswordEditText.getEnterText(),
                )
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