package com.argusoft.who.emcare.data.local.pref

import ca.uhn.fhir.context.FhirContext
import ca.uhn.fhir.context.FhirVersionEnum
import com.argusoft.who.emcare.ui.common.model.LoggedInUser
import com.argusoft.who.emcare.ui.common.model.User
import com.argusoft.who.emcare.utils.extention.fromJson
import com.argusoft.who.emcare.utils.extention.orEmpty
import com.argusoft.who.emcare.utils.extention.toJson
import org.hl7.fhir.r4.model.Bundle
import org.hl7.fhir.r4.model.ResourceType

class PreferenceManager(private val sharedPreferences: EncPref) : Preference {

    companion object {
        private const val IS_LOGIN = "pref_is_login"
        private const val USER = "USER"
        private const val TOKEN = "TOKEN"
        private const val FACILITY_ID = "FACILITY_ID"
        private const val LOGGED_IN_USER = "LOGGED_IN_USER"
        private const val EMCARE_LAST_SYNC_TIME_STAMP = "EMCARE_LAST_SYNC_TIME_STAMP"
        private const val SUBMITTED_RESOURCE = "SUBMITTED_RESOURCE"
        private const val THEME = "THEME"
        private const val COUNTRY = "COUNTRY"

    }

    val parser = FhirContext.forCached(FhirVersionEnum.R4).newJsonParser()

    override fun setLogin() {
        sharedPreferences.putBoolean(IS_LOGIN, true)
    }

    override fun isLogin(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGIN, false)
    }

    override fun setToken(token: String) {
        sharedPreferences.putString(TOKEN, token)
    }

    override fun getToken(): String {
        return sharedPreferences.getString(TOKEN, "")
    }

    override fun setUser(user: User) {
        sharedPreferences.putString(USER, user.toJson())
    }

    override fun getUser(): User? {
        return sharedPreferences.getString(USER).orEmpty { "{}" }.fromJson<User>()
    }

    override fun setFacilityId(facilityId: String) {
        sharedPreferences.putString(FACILITY_ID, facilityId)
    }

    override fun getFacilityId(): String {
        return sharedPreferences.getString(FACILITY_ID, "")
    }

    override fun setLoggedInUser(loggedInUser: LoggedInUser) {
        sharedPreferences.putString(LOGGED_IN_USER, loggedInUser.toJson())
    }

    override fun getLoggedInUser(): LoggedInUser? {
        return sharedPreferences.getString(LOGGED_IN_USER).orEmpty { "{}" }.fromJson<LoggedInUser>()
    }

    override fun getLastSyncTimestamp(): String {
        return sharedPreferences.getString(EMCARE_LAST_SYNC_TIME_STAMP,"")
    }

    override fun writeLastSyncTimestamp(timestamp: String) {
        sharedPreferences.putString(EMCARE_LAST_SYNC_TIME_STAMP, timestamp)
    }

    override fun getSubmittedResource(): Bundle? {
        val submittedResource = sharedPreferences.getString(SUBMITTED_RESOURCE)
        return if(submittedResource.isNotEmpty()) {
            parser.parseResource(Bundle::class.java, submittedResource) as Bundle
        } else {
            null
        }
    }

    override fun getSubmittedResourceAsString(): String? {
        val submittedResource = getSubmittedResource()
        return if(submittedResource != null) {
            parser.encodeResourceToString(submittedResource)
        } else {
            null
        }
    }

    override fun setSubmittedResource(bundle: Bundle) {
        sharedPreferences.putString(SUBMITTED_RESOURCE, parser.encodeResourceToString(bundle))
    }

    override fun setTheme(theme: Int) {
        sharedPreferences.putInt(THEME, theme)
    }

    override fun getTheme(): Int {
        return sharedPreferences.getInt(THEME, 0)
    }

    override fun setCountry(country: String) {
        sharedPreferences.putString(COUNTRY, country)
    }

    override fun getCountry(): String {
        return sharedPreferences.getString(COUNTRY, "")
    }

    override fun clear() {
        //Tweaked to persist facilityId & country
        val facilityId = sharedPreferences.getString(FACILITY_ID, "")
        val country = sharedPreferences.getString(COUNTRY, "")
        val lastSyncTimeStamp = sharedPreferences.getString(EMCARE_LAST_SYNC_TIME_STAMP,"")
        sharedPreferences.clear()
        setFacilityId(facilityId)
        setCountry(country)
        writeLastSyncTimestamp(lastSyncTimeStamp)
    }
}
