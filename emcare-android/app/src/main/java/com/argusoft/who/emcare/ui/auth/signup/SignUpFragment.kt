package com.argusoft.who.emcare.ui.auth.signup

import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentSignupBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.Location
import com.argusoft.who.emcare.ui.common.model.Role
import com.argusoft.who.emcare.utils.extention.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignupBinding>() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun initView() {

    }

    private fun setupLocationAutoComplete(locationList: List<Location>) {
        binding.locationTextInputLayout.tag = locationList
        binding.locationEditText.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.select_dialog_item,
                locationList.map { it.name }
            )
        )
    }

    private fun setupRoleAutoComplete(roleList: List<Role>) {
        binding.roleTextInputLayout.tag = roleList
        binding.roleEditText.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.select_dialog_item,
                roleList.map { it.name }
            )
        )
    }

    override fun initListener() {
        binding.nextButton.setOnClickListener(this)
        binding.submitButton.setOnClickListener(this)
        binding.headerLayout.toolbar.setNavigationOnClickListener(this)
        binding.locationEditText.setOnItemClickListener { parent, view, position, id ->
            binding.locationEditText.tag = (binding.locationTextInputLayout.tag as? List<Location>)?.getOrNull(position)?.id
        }
        binding.roleEditText.setOnItemClickListener { parent, view, position, id ->
            binding.roleEditText.tag = (binding.roleTextInputLayout.tag as? List<Role>)?.getOrNull(position)?.name
        }
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
        observeNotNull(signUpViewModel.locationAndRolesApiState) { pair ->
            pair.first.handleApiView(binding.progressLayout) {
                it?.let { list -> setupLocationAutoComplete(list) }
            }
            pair.second.whenSuccess {
                setupRoleAutoComplete(it)
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
                    binding.locationEditText.tag as? Int,
                    binding.roleEditText.tag as? String,
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