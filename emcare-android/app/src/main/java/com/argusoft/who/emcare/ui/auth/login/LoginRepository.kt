package com.argusoft.who.emcare.ui.auth.login

import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.EncPref
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.DeviceDetails
import com.argusoft.who.emcare.ui.common.model.LoggedInUser
import com.argusoft.who.emcare.ui.common.model.User
import com.argusoft.who.emcare.utils.common.NetworkHelper
import com.argusoft.who.emcare.utils.extention.whenResult
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.google.android.fhir.FhirEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val api: Api,
    private val database: Database,
    private val preference: Preference,
    private val networkHelper: NetworkHelper,
    private val fhirEngine: FhirEngine,
) {

    fun login(requestMap: Map<String, String>, deviceDetails: DeviceDetails) = flow {
        if (networkHelper.isInternetAvailable()) {
            val loginResponse = api.login(requestMap)
            loginResponse.whenResult(onSuccess = { user ->

                //Set preference data
                preference.setLogin()
                user.accessToken?.let { accessToken -> preference.setToken(accessToken) }
                preference.setUser(user)

                //Get User Data
                api.getLoggedInUser().whenSuccess { loggedInUser ->
                    if (!preference.getFacilityId().equals(loggedInUser.facility!![0].facilityId)){
                        database.deleteAllConsultations()
                        CoroutineScope(Dispatchers.IO).launch {
                            fhirEngine.clearDatabase()
                        }
                    }
                    preference.setFacilityId(loggedInUser.facility!![0].facilityId)
                    preference.setLoggedInUser(loggedInUser)
                    database.saveLoginUser(loggedInUser.apply {
                        this.password = requestMap["password"]?.let { EncPref.encrypt(it) }
                    })
                }

                //Get Device Block or not
                val getDevice = deviceDetails.deviceUUID?.let { api.getDevice(it) }
                getDevice?.whenResult(onSuccess = {
                    deviceDetails.isBlocked = it.isBlocked
                    api.addDevice(deviceDetails)
                    if (it.isBlocked == true) {
                        emit(ApiResponse.ApiError(apiErrorMessageResId = R.string.blocked_device_message))
                    } else {
                        emit(loginResponse)
                    }
                }, onFailed = {
                    api.addDevice(deviceDetails)
                    emit(loginResponse)
                })
            }, onFailed = {
                emit(loginResponse)
            })
        } else {
            val loginUser = database.getAllUser()
            loginUser?.find {
                it.email == requestMap["username"] && EncPref.decrypt(it.password ?: "") == requestMap["password"]
            }?.let {

                //Set preference data
                preference.setLogin()
                preference.setLoggedInUser(it)
                emit(ApiResponse.Success(data = null))

            } ?: emit(ApiResponse.ApiError(apiErrorMessageResId = R.string.error_msg_not_find_user))
        }
    }

}