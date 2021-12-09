package com.argusoft.who.emcare.ui.auth.login

import android.content.Intent
import android.view.View
import androidx.fragment.app.viewModels
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.databinding.FragmentLoginBinding
import com.argusoft.who.emcare.ui.common.base.BaseFragment
import com.argusoft.who.emcare.ui.home.HomeActivity
import com.argusoft.who.emcare.utils.extention.*
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    private val loginViewModel: LoginViewModel by viewModels()

    override fun initView() {
    }

    override fun initListener() {
        binding.loginButton.setOnClickListener(this)
        binding.signupTextView.setOnClickListener(this)
    }

    override fun initObserver() {
        observeNotNull(loginViewModel.errorMessageState) {
            context?.showSnackBar(
                view = binding.progressLayout,
                message = getString(it),
                isError = true
            )
        }
        observeNotNull(loginViewModel.loginApiState) {
            it.handleApiView(binding.progressLayout) {
                startActivity(Intent(requireContext(), HomeActivity::class.java))
                requireActivity().finish()
            }
        }
    }

    override fun onClick(view: View?) {
        super.onClick(view)
        when (view?.id) {
            R.id.loginButton -> {
                loginViewModel.login(
                    binding.usernameEditText.getEnterText(),
                    binding.passwordEditText.getEnterText()
                )
            }
            R.id.signupTextView -> {
                navigate(R.id.action_loginFragment_to_signUpFragment)
            }
        }
    }
}