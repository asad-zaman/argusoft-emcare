package com.argusoft.who.emcare.ui.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.Location
import com.argusoft.who.emcare.ui.common.model.Role
import com.argusoft.who.emcare.ui.common.model.SignupRequest
import com.argusoft.who.emcare.utils.extention.get
import com.argusoft.who.emcare.utils.extention.isValidEmail
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val api: Api,
    private val database: Database,
    private val preference: Preference
) : ViewModel() {

    private val _errorMessageState = SingleLiveEvent<Int>()
    val errorMessageState: LiveData<Int> = _errorMessageState

    private val signupRequest = SignupRequest()

    private val _signupApiState = SingleLiveEvent<ApiResponse<Any>>()
    val signupApiState: LiveData<ApiResponse<Any>> = _signupApiState

    private val _locationAndRolesApiState = MutableLiveData<Pair<ApiResponse<List<Location>>, ApiResponse<List<Role>>>>()
    val locationAndRolesApiState: LiveData<Pair<ApiResponse<List<Location>>, ApiResponse<List<Role>>>> = _locationAndRolesApiState

    init {
        getLocationsAndRoles()
    }

    fun getLocationsAndRoles() {
        _locationAndRolesApiState.value = Pair(ApiResponse.Loading(), ApiResponse.Loading())
        viewModelScope.launch {
            _locationAndRolesApiState.value = Pair(async {
                api.getLocations().whenSuccess {
                    database.saveLocations(it)
                }
            }.await(), async {
                api.getRoles()
            }.await())
        }
    }

    fun validateSignup(
        firstname: String,
        lastname: String,
        email: String,
        locationId: Int?,
        roleName: String?,
    ) {
        when {
            firstname.isEmpty() -> _errorMessageState.value = R.string.error_msg_firstname
            lastname.isEmpty() -> _errorMessageState.value = R.string.error_msg_lastname
            email.isEmpty() -> _errorMessageState.value = R.string.error_msg_email
            email.isNotEmpty() && !email.isValidEmail() -> _errorMessageState.value = R.string.error_msg_valid_email
            locationId.get() == -1 -> _errorMessageState.value = R.string.error_msg_location
            roleName.isNullOrEmpty() -> _errorMessageState.value = R.string.error_msg_role
            else -> {
                signupRequest.firstName = firstname
                signupRequest.lastName = lastname
                signupRequest.email = email
                signupRequest.locationId = locationId
                signupRequest.roleName = roleName
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