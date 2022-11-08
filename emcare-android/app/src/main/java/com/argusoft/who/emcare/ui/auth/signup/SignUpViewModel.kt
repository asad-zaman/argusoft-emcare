package com.argusoft.who.emcare.ui.auth.signup

import androidx.lifecycle.*
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.DEFAULT_COUNTRY_CODE
import com.argusoft.who.emcare.ui.common.DEFAULT_USER_ROLE
import com.argusoft.who.emcare.ui.common.model.Facility
import com.argusoft.who.emcare.ui.common.model.Role
import com.argusoft.who.emcare.ui.common.model.SignupRequest
import com.argusoft.who.emcare.utils.extention.isValidEmail
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpRepository: SignUpRepository
) : ViewModel() {

    private val _errorMessageState = SingleLiveEvent<Int>()
    val errorMessageState: LiveData<Int> = _errorMessageState

    private val signupRequest = SignupRequest()

    private val _signupApiState = SingleLiveEvent<ApiResponse<Any>>()
    val signupApiState: LiveData<ApiResponse<Any>> = _signupApiState

    private val _facilityAndRolesApiState = MutableLiveData<Pair<ApiResponse<List<Facility>>, ApiResponse<List<Role>>>>()
    val facilityAndRolesApiState: LiveData<Pair<ApiResponse<List<Facility>>, ApiResponse<List<Role>>>> = _facilityAndRolesApiState

    private val _facilityApiState = MutableLiveData<ApiResponse<List<Facility>>>()
    val facilityApiState: LiveData<ApiResponse<List<Facility>>> = _facilityApiState

    init {
//        getFacilitiesAndRoles()
        getFacilities()
    }

//    private fun getFacilitiesAndRoles() {
//        _facilityAndRolesApiState.value = Pair(ApiResponse.Loading(), ApiResponse.Loading())
//        viewModelScope.launch {
//            signUpRepository.getFacilitiesAndRoles().collect {
//                _facilityAndRolesApiState.value = it
//            }
//        }
//    }

    private fun getFacilities() {
        _facilityAndRolesApiState.value = Pair(ApiResponse.Loading(), ApiResponse.Loading())
        viewModelScope.launch {
            signUpRepository.getFacilities().collect {
                _facilityApiState.value = it
            }
        }
    }

    fun validateSignup(
        firstname: String,
        lastname: String,
        email: String,
        facilityId: String,
        phone: String,
//        roleName: String?,
    ) {
        when {
            firstname.isEmpty() -> _errorMessageState.value = R.string.error_msg_firstname
            lastname.isEmpty() -> _errorMessageState.value = R.string.error_msg_lastname
            email.isEmpty() -> _errorMessageState.value = R.string.error_msg_email
            email.isNotEmpty() && !email.isValidEmail() -> _errorMessageState.value = R.string.error_msg_valid_email
            facilityId.isEmpty() -> _errorMessageState.value = R.string.error_msg_location
            phone.isEmpty() -> _errorMessageState.value = R.string.error_msg_phone_number
//            roleName.isNullOrEmpty() -> _errorMessageState.value = R.string.error_msg_role
            else -> {
                signupRequest.firstName = firstname
                signupRequest.lastName = lastname
                signupRequest.email = email
                signupRequest.facilityIds = listOf(facilityId)
                signupRequest.roleName = DEFAULT_USER_ROLE
                signupRequest.phone = phone
                signupRequest.countryCode = DEFAULT_COUNTRY_CODE
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
                    signUpRepository.signUp(signupRequest).collect {
                        _signupApiState.value = it
                    }
                }
            }
        }
    }
}