package com.argusoft.who.emcare.data.remote

import com.argusoft.who.emcare.oldstruct.model.DeviceInfo
import com.argusoft.who.emcare.ui.common.model.DeviceDetails
import com.argusoft.who.emcare.ui.common.model.User

interface Api {

    suspend fun login(requestMap: Map<String, String>): ApiResponse<User>

    suspend fun addDevice(deviceInfo: DeviceDetails): ApiResponse<DeviceDetails>

    suspend fun getDevice(deviceUUID: String): ApiResponse<DeviceDetails>
}
