package com.argusoft.who.emcare.ui.auth.login

import androidx.lifecycle.*
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.KEYCLOAK_CLIENT_ID
import com.argusoft.who.emcare.ui.common.KEYCLOAK_CLIENT_SECRET
import com.argusoft.who.emcare.ui.common.KEYCLOAK_GRANT_TYPE
import com.argusoft.who.emcare.ui.common.KEYCLOAK_SCOPE
import com.argusoft.who.emcare.ui.common.model.User
import com.argusoft.who.emcare.utils.extention.whenSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val api: Api,
    private val preference: Preference
) : ViewModel() {

    private val _errorMessageState = MutableLiveData<Int>()
    val errorMessageState: LiveData<Int> = _errorMessageState

    private val _loginApiState = MutableLiveData<ApiResponse<User>>()
    val loginApiState: LiveData<ApiResponse<User>> = _loginApiState

    fun login(
        username: String,
        password: String
    ) {
        when {
            username.isEmpty() -> _errorMessageState.value = R.string.error_msg_username
            password.isEmpty() -> _errorMessageState.value = R.string.error_msg_password
            else -> {
                val requestMap = HashMap<String,String>()
                requestMap["client_id"] = KEYCLOAK_CLIENT_ID
                requestMap["grant_type"] = KEYCLOAK_GRANT_TYPE
                requestMap["client_secret"] = KEYCLOAK_CLIENT_SECRET
                requestMap["scope"] = KEYCLOAK_SCOPE
                requestMap["username"] = username
                requestMap["password"] = password
                _loginApiState.value = ApiResponse.Loading()
                viewModelScope.launch {
                    _loginApiState.value = api.login(requestMap).whenSuccess {
                        preference.setLogin()
                        it.accessToken?.let { accessToken -> preference.setToken(accessToken) }
                        preference.setUser(it)
                    }
                }
            }
        }
    }
}