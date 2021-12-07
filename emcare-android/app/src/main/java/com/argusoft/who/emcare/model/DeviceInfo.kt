package com.argusoft.who.emcare.model

import com.google.gson.annotations.SerializedName

class DeviceInfo (
    private var _createdBy: String?,
    private var _createdOn: String?,
    private var _modifiedBy: String?,
    private var _modifiedOn: String?,
    private var _deviceId: Int?,
    private var _androidVersion : String,
    private var _imeiNumber : String,
    private var _macAddress : String,
    private var _lastLoggedInUser : String?,
    private var _isBlocked : Boolean,
) {
    @SerializedName("createdBy")
    private var createdBy: String?

    @SerializedName("createdOn")
    private var createdOn: String?

    @SerializedName("modifiedBy")
    private var modifiedBy: String?

    @SerializedName("modifiedOn")
    private var modifiedOn: String?

    @SerializedName("deviceId")
    private var deviceId: Int?

    @SerializedName("androidVersion")
    private var androidVersion: String

    @SerializedName("imeiNumber")
    private var imeiNumber: String

    @SerializedName("macAddress")
    private var macAddress: String

    @SerializedName("lastLoggedInUser")
    private var lastLoggedInUser: String?

    @SerializedName("isBlocked")
    private var isBlocked: Boolean

    init {
        this.createdBy = _createdBy
        this.createdOn = _createdOn
        this.modifiedBy = _modifiedBy
        this.modifiedOn = _modifiedOn
        this.deviceId = _deviceId
        this.imeiNumber = _imeiNumber
        this.macAddress = _macAddress
        this.lastLoggedInUser = _lastLoggedInUser
        this.androidVersion = _androidVersion
        this.isBlocked = _isBlocked
    }

    public fun getCreatedBy(): String? {
        return createdBy
    }

    public fun setCreatedBy(createdBy: String?) {
        this.createdBy = createdBy
    }

    public fun getCreatedOn(): String? {
        return createdOn
    }

    public fun setCreatedOn(createdOn: String?) {
        this.createdOn = createdOn
    }

    public fun getModifiedBy(): String? {
        return modifiedBy
    }

    public fun setModifiedBy(modifiedBy: String?) {
        this.modifiedBy = modifiedBy
    }

    public fun getModifiedOn(): String? {
        return modifiedOn
    }

    public fun setModifiedOn(modifiedOn: String?) {
        this.modifiedOn = modifiedOn
    }

    public fun getDeviceId(): Int? {
        return deviceId
    }

    public fun setDeviceId(deviceId: Int?) {
        this.deviceId = deviceId
    }

    public fun getAndroidVersion(): String {
        return androidVersion
    }

    public fun setAndroidVersion(androidVersion: String) {
        this.androidVersion = androidVersion
    }

    public fun getImeiNumber(): String {
        return imeiNumber
    }

    public fun setImeiNumber(imeiNumber: String) {
        this.imeiNumber = imeiNumber
    }

    public fun getMacAddress(): String {
        return macAddress
    }

    public fun setMacAddress(macAddress: String) {
        this.macAddress = macAddress
    }

    public fun getLastLoggedInUser(): String? {
        return lastLoggedInUser
    }

    public fun setLastLoggedInUser(lastLoggedInUser: String?) {
        this.lastLoggedInUser = lastLoggedInUser
    }

    public fun getIsBlocked(): Boolean {
        return isBlocked
    }

    public fun setIsBlocked(isBlocked: Boolean) {
        this.isBlocked = isBlocked
    }

}