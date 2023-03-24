package com.argusoft.who.emcare.ui.auth.login

import androidx.lifecycle.*
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.KEYCLOAK_CLIENT_ID
import com.argusoft.who.emcare.ui.common.KEYCLOAK_CLIENT_SECRET
import com.argusoft.who.emcare.ui.common.KEYCLOAK_GRANT_TYPE
import com.argusoft.who.emcare.ui.common.KEYCLOAK_SCOPE
import com.argusoft.who.emcare.ui.common.model.DeviceDetails
import com.argusoft.who.emcare.ui.common.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.set

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginRepository: LoginRepository
) : ViewModel() {

    private val _errorMessageState = MutableLiveData<Int>()
    val errorMessageState: LiveData<Int> = _errorMessageState

    private val _loginApiState = MutableLiveData<ApiResponse<User>>()
    val loginApiState: LiveData<ApiResponse<User>> = _loginApiState

    fun login(
        username: String,
        password: String,
        deviceName: String,
        deviceOS: String,
        deviceModel: String,
        deviceUUID: String,
        androidVersion: String
    ) {
        val deviceDetails = DeviceDetails(
            androidVersion = androidVersion,
            deviceName = deviceName,
            deviceOs = deviceOS,
            deviceModel = deviceModel,
            deviceUUID = deviceUUID,
            isBlocked = false,
            imeiNumber = "",
            macAddress = "",
            lastLoggedInUser = ""
        )
        when {
            username.isEmpty() -> _errorMessageState.value = R.string.error_msg_username
            password.isEmpty() -> _errorMessageState.value = R.string.error_msg_password
            else -> {
                _loginApiState.value = ApiResponse.Loading()
                val requestMap = HashMap<String, String>()
                requestMap["client_id"] = KEYCLOAK_CLIENT_ID
                requestMap["grant_type"] = KEYCLOAK_GRANT_TYPE
                requestMap["client_secret"] = KEYCLOAK_CLIENT_SECRET
                requestMap["scope"] = KEYCLOAK_SCOPE
                requestMap["username"] = username
                requestMap["password"] = password
                viewModelScope.launch {
                    loginRepository.login(requestMap, deviceDetails).collect {
                        _loginApiState.value = it
                    }
                }
            }
        }
    }

    fun addDevice(
        deviceName: String,
        deviceOS: String,
        deviceModel: String,
        deviceUUID: String,
        androidVersion: String
    ) {
        val deviceDetails = DeviceDetails(
            androidVersion = androidVersion,
            deviceName = deviceName,
            deviceOs = deviceOS,
            deviceModel = deviceModel,
            deviceUUID = deviceUUID,
            isBlocked = false,
            imeiNumber = "",
            macAddress = "",
            lastLoggedInUser = ""
        )
        viewModelScope.launch {
            loginRepository.addDevice(deviceDetails)
        }
    }
}