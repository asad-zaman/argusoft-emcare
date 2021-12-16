package com.argusoft.who.emcare.ui.auth.signup

import androidx.lifecycle.*
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.KEYCLOAK_CLIENT_ID
import com.argusoft.who.emcare.ui.common.KEYCLOAK_CLIENT_SECRET
import com.argusoft.who.emcare.ui.common.KEYCLOAK_GRANT_TYPE
import com.argusoft.who.emcare.ui.common.KEYCLOAK_SCOPE
import com.argusoft.who.emcare.ui.common.model.SignupRequest
import com.argusoft.who.emcare.ui.common.model.User
import com.argusoft.who.emcare.utils.extention.get
import com.argusoft.who.emcare.utils.extention.isValidEmail
import com.argusoft.who.emcare.utils.extention.whenSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val api: Api,
    private val preference: Preference
) : ViewModel() {

    private val _errorMessageState = MutableLiveData<Int>()
    val errorMessageState: LiveData<Int> = _errorMessageState

    private val signupRequest = SignupRequest()

    private val _signupApiState = MutableLiveData<ApiResponse<Any>>()
    val signupApiState: LiveData<ApiResponse<Any>> = _signupApiState

    fun validateSignup(
        firstname: String,
        lastname: String,
        email: String,
        locationId: Int?,
        roleId: Int?,
    ) {
        when {
            firstname.isEmpty() -> _errorMessageState.value = R.string.error_msg_firstname
            lastname.isEmpty() -> _errorMessageState.value = R.string.error_msg_lastname
            email.isEmpty() -> _errorMessageState.value = R.string.error_msg_email
            email.isNotEmpty() && !email.isValidEmail() -> _errorMessageState.value = R.string.error_msg_valid_email
            locationId.get() == -1 -> _errorMessageState.value = R.string.error_msg_location
            roleId.get() == -1 -> _errorMessageState.value = R.string.error_msg_role
            else -> {
                signupRequest.firstName = firstname
                signupRequest.lastName = lastname
                signupRequest.email = email
                signupRequest.locationId = locationId
                _errorMessageState.value = 0
            }
        }
    }

    fun signup(
        password: String,
        confirmPassword: String
    ) {
        when {
            password.isEmpty() -> _errorMessageState.value = R.string.error_msg_password
            confirmPassword.isEmpty() -> _errorMessageState.value = R.string.error_msg_confirm_password
            confirmPassword != password -> _errorMessageState.value = R.string.error_msg_password_not_match
            else -> {
                signupRequest.password = password
                _signupApiState.value = ApiResponse.Loading()
                viewModelScope.launch {
                    _signupApiState.value = api.signup(signupRequest)
                }
            }
        }
    }
}