package com.argusoft.who.emcare.ui.auth.signup

import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentSignupBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.common.model.Facility
import com.argusoft.who.emcare.utils.extention.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : BaseFragment<FragmentSignupBinding>() {

    private val signUpViewModel: SignUpViewModel by viewModels()

    override fun initView() {
        //No Initialization Required
    }

    private fun setupFacilityAutoComplete(facilityList: List<Facility>) {
        binding.facilityTextInputLayout.tag = facilityList
        binding.facilityEditText.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.select_dialog_item,
                facilityList.map { it.facilityName }
            )
        )
    }

//    private fun setupRoleAutoComplete(roleList: List<Role>) {
//        binding.roleTextInputLayout.tag = roleList
//        binding.roleEditText.setAdapter(
//            ArrayAdapter(
//                requireContext(), android.R.layout.select_dialog_item,
//                roleList.map { it.name }
//            )
//        )
//    }

    override fun initListener() {
        binding.submitButton.setOnClickListener(this)
        binding.headerLayout.toolbar.setNavigationOnClickListener(this)
        binding.facilityEditText.setOnItemClickListener { _, _, position, _ ->
            binding.facilityEditText.tag = (binding.facilityTextInputLayout.tag as? List<Facility>)?.getOrNull(position)?.facilityId
        }
//        binding.roleEditText.setOnItemClickListener { parent, view, position, id ->
//            binding.roleEditText.tag = (binding.roleTextInputLayout.tag as? List<Role>)?.getOrNull(position)?.name
//        }
    }

    override fun initObserver() {
        observeNotNull(signUpViewModel.errorMessageState) {
            if (it == 0) {
                signUpViewModel.signup(
                    binding.passwordEditText.getEnterText(),
                    binding.confirmPasswordEditText.getEnterText(),
                )
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

        observeNotNull(signUpViewModel.facilityApiState) { it ->
            it.handleApiView(binding.progressLayout) {
                it?.let { list -> setupFacilityAutoComplete(list) }
            }
        }
//        observeNotNull(signUpViewModel.facilityAndRolesApiState) { pair ->
//            pair.first.handleApiView(binding.progressLayout) {
//                it?.let { list -> setupFacilityAutoComplete(list) }
//            }
//            pair.second.whenSuccess {
//                setupRoleAutoComplete(it)
//            }
//        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.submitButton -> {
                signUpViewModel.validateSignup(
                    binding.firstNameEditText.getEnterText(),
                    binding.lastNameEditText.getEnterText(),
                    binding.emailEditText.getEnterText(),
                    if (binding.facilityEditText.tag!= null) binding.facilityEditText.tag as String else "",
                    binding.phoneEditText.getEnterText())
            }
            else -> {
                requireActivity().onBackPressed()
            }
        }
    }
}