package com.argusoft.who.emcare.ui.auth.login

import android.util.Log
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.EncPref
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.DeviceDetails
import com.argusoft.who.emcare.ui.common.model.User
import com.argusoft.who.emcare.utils.common.NetworkHelper
import com.argusoft.who.emcare.utils.extention.whenResult
import com.argusoft.who.emcare.utils.extention.whenSuccess
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.search
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.internal.wait
import org.hl7.fhir.r4.model.PlanDefinition
import java.util.*
import javax.inject.Inject

class LoginRepository @Inject constructor(
    private val api: Api,
    private val database: Database,
    private val preference: Preference,
    private val networkHelper: NetworkHelper,
    private val fhirEngine: FhirEngine,
) {

    fun clearData() {
        if (preference.getFacilityId().isEmpty()){
            CoroutineScope(Dispatchers.IO).launch {
                database.deleteAllConsultations()
                fhirEngine.clearDatabase()
            }
            preference.clearAll()
        }
    }

    fun getLoggedInUser(loginResponse: ApiResponse<User>, user: User, requestMap: Map<String, String>, deviceDetails: DeviceDetails) = flow {
        val loggedInUserResponse = api.getLoggedInUser()
        loggedInUserResponse.whenResult(onSuccess = { loggedInUser ->
            if(loggedInUser.facility == null || loggedInUser.facility!!.isEmpty()){
                preference.clearAll()
                emit(ApiResponse.ApiError(apiErrorMessageResId = R.string.error_facility_not_assigned_user))
            }else {
                //Set preference data
                if (preference.getFacilityId() != loggedInUser.facility!![0].facilityId || preference.getFacilityId()
                        .isEmpty()
                ) {
                    database.deleteAllConsultations()
                    CoroutineScope(Dispatchers.IO).launch {
                        fhirEngine.clearDatabase()
                    }
                    preference.writeLastSyncTimestamp("") //Since its a new user and database is cleared it will require complete sync.
                }
                preference.setFacilityId(loggedInUser.facility!![0].facilityId)
                preference.setLoggedInUser(loggedInUser)
                user.applicationAgent?.let { preference.setCountry(it) }
                database.saveLoginUser(loggedInUser.apply {
                    this.password = requestMap["password"]?.let { EncPref.encrypt(it) }
                })
            }

            //Get Device Block or not
            val getDevice = deviceDetails.deviceUUID?.let { api.getDevice(it) }
            getDevice?.whenResult(onSuccess = {
                deviceDetails.isBlocked = it.isBlocked
                CoroutineScope(Dispatchers.IO).launch  {
                    val planDefinitions = fhirEngine.search<PlanDefinition> {
                        sort(PlanDefinition.DATE, Order.ASCENDING)
                    }
                    if(planDefinitions.isNotEmpty())
                        deviceDetails.igVersion = planDefinitions.last().version
                    api.addDevice(deviceDetails)
                }

                if (it.isBlocked == true) {
                    emit(ApiResponse.ApiError(apiErrorMessageResId = R.string.blocked_device_message))
                } else {
                    emit(loginResponse)
                }
            }, onFailed = {
                CoroutineScope(Dispatchers.IO).launch  {
                    val planDefinitions = fhirEngine.search<PlanDefinition> {
                        sort(PlanDefinition.DATE, Order.ASCENDING)
                    }
                    if(planDefinitions.isNotEmpty())
                        deviceDetails.igVersion = planDefinitions.last().version
                    api.addDevice(deviceDetails)
                }
                emit(loginResponse)
            })
        }, onFailed = {
            emit(loginResponse)
        })
    }

    fun login(requestMap: Map<String, String>, deviceDetails: DeviceDetails) = flow {
        if (networkHelper.isInternetAvailable()) {
            val loginResponse = api.login(requestMap)
            loginResponse.whenResult(onSuccess = { user ->

                //Set preference data
                preference.setLogin()
                user.accessToken?.let { accessToken -> preference.setToken(accessToken) }
                preference.setUser(user)

//                emit(loginResponse)

//                //Get User Data
                api.getLoggedInUser().whenSuccess { loggedInUser ->
                    if(loggedInUser.facility == null || loggedInUser.facility!!.isEmpty()){
                        preference.clearAll()
                        emit(ApiResponse.ApiError(apiErrorMessageResId = R.string.error_facility_not_assigned_user))
                    }else {
                        if (preference.getFacilityId() != loggedInUser.facility!![0].facilityId || preference.getFacilityId()
                                .isEmpty()
                        ) {
                            database.deleteAllConsultations()
                            preference.writeLastSyncTimestamp("") //Since its a new user and database is cleared it will require complete sync.
                            CoroutineScope(Dispatchers.IO).launch {
                                fhirEngine.clearDatabase()
                                preference.setFacilityId(loggedInUser.facility!![0].facilityId)
                                preference.setLoggedInUser(loggedInUser)
                                user.applicationAgent?.let { preference.setCountry(it) }
                                database.saveLoginUser(loggedInUser.apply {
                                    this.password = requestMap["password"]?.let { EncPref.encrypt(it) }
                                })
                            }

                        }else{
                            preference.setFacilityId(loggedInUser.facility!![0].facilityId)
                            preference.setLoggedInUser(loggedInUser)
                            user.applicationAgent?.let { preference.setCountry(it) }
                            database.saveLoginUser(loggedInUser.apply {
                                this.password = requestMap["password"]?.let { EncPref.encrypt(it) }
                            })
                        }

                    }
                }

                //Get Device Block or not
                val getDevice = deviceDetails.deviceUUID?.let { api.getDevice(it) }
                getDevice?.whenResult(onSuccess = {
                    deviceDetails.isBlocked = it.isBlocked
//                    CoroutineScope(Dispatchers.IO).launch  {
//                        val planDefinitions = fhirEngine.search<PlanDefinition> {
//                            sort(PlanDefinition.DATE, Order.ASCENDING)
//                        }
//                        if(planDefinitions.isNotEmpty())
//                            deviceDetails.igVersion = planDefinitions.last().version
//                        api.addDevice(deviceDetails)
//                    }

                    if (it.isBlocked == true) {
                        emit(ApiResponse.ApiError(apiErrorMessageResId = R.string.blocked_device_message))
                    } else {
                        emit(loginResponse)
                    }
                }, onFailed = {
//                    CoroutineScope(Dispatchers.IO).launch  {
//                        val planDefinitions = fhirEngine.search<PlanDefinition> {
//                            sort(PlanDefinition.DATE, Order.ASCENDING)
//                        }
//                        if(planDefinitions.isNotEmpty())
//                            deviceDetails.igVersion = planDefinitions.last().version
//                        api.addDevice(deviceDetails)
//                    }
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

    fun addDevice(deviceDetails: DeviceDetails) {
        if (networkHelper.isInternetAvailable()) {
            CoroutineScope(Dispatchers.IO).launch {
                val planDefinitions = fhirEngine.search<PlanDefinition> {
                    sort(PlanDefinition.DATE, Order.ASCENDING)
                }
                if (planDefinitions.isNotEmpty())
                    deviceDetails.igVersion = planDefinitions.last().version
                api.addDevice(deviceDetails)
            }
        }

    }

}