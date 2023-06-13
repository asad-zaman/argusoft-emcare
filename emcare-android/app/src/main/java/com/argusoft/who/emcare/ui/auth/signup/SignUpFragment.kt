package com.argusoft.who.emcare.ui.auth.signup

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.BackgroundColorSpan
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.BuildConfig.BASE_URL_TERMS
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
        val termsMsg:String = getString(R.string.text_terms_and_policy_msg)
        val termsText: String = getString(R.string.text_terms_and_policy)
        val builder = SpannableStringBuilder(termsMsg)
        builder.append(" ")
        builder.append(termsText)

        builder.setSpan(ForegroundColorSpan(Color.BLUE), termsMsg.length + 1, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        builder.setSpan(object : ClickableSpan() {
            override fun onClick(view: View) {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(BASE_URL_TERMS + "#/termsAndConditions"))
                startActivity(intent)
            }
        }, termsMsg.length + 1, builder.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.termsAndPolicyTextView.text = builder
        binding.termsAndPolicyTextView.movementMethod = LinkMovementMethod.getInstance()

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

    private fun setupCountryAutoComplete(countryList: List<String>) {
        binding.countryTextInputLayout.tag = countryList
        binding.countryEditText.setAdapter(
            ArrayAdapter(
                requireContext(), android.R.layout.select_dialog_item,
                countryList.map { it }
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
        binding.countryEditText.setOnItemClickListener { _, _, position, _ ->
            binding.countryEditText.tag = (binding.countryTextInputLayout.tag as? List<String>)?.getOrNull(position)
            preference.setSelectedCountry(binding.countryEditText.tag as String)
            signUpViewModel.getFacilities()
            binding.facilityEditText.text.clear()
        }
//        binding.roleEditText.setOnItemClickListener { parent, view, position, id ->
//            binding.roleEditText.tag = (binding.roleTextInputLayout.tag as? List<Role>)?.getOrNull(position)?.name
//        }
    }

    override fun initObserver() {
        observeNotNull(signUpViewModel.errorMessageState) {
            if (it == 0) {
//                preference.setSelectedCountry("")
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
        observeNotNull(signUpViewModel.countryApiState) { it ->
            it.handleApiView(binding.progressLayout) {
                it?.let { list -> setupCountryAutoComplete(list) }
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
                    if (binding.countryEditText.tag!= null) binding.countryEditText.tag as String else "",
                    if (binding.facilityEditText.tag!= null) binding.facilityEditText.tag as String else "",
                    binding.phoneEditText.getEnterText(),
                    binding.termsAndPolicyTextView.isChecked)
            }
            else -> {
                requireActivity().onBackPressed()
            }
        }
    }
}