package com.argusoft.who.emcare.data.local.pref

import com.argusoft.who.emcare.ui.common.model.LoggedInUser
import com.argusoft.who.emcare.ui.common.model.User
import org.hl7.fhir.r4.model.Bundle

interface Preference {

    fun setLogin()

    fun isLogin(): Boolean

    fun setToken(token: String)

    fun getToken(): String

    fun setFacilityId(facilityId: String)

    fun getFacilityId(): String

    fun setUser(user: User)

    fun getUser(): User?

    fun setSelectedCountry(selectedCountry: String)

    fun getSelectedCountry(): String

    fun setLoggedInUser(loggedInUser: LoggedInUser)

    fun getLoggedInUser() : LoggedInUser?

    fun getLastSyncTimestamp(): String

    fun writeLastSyncTimestamp(timestamp: String)

    fun getSubmittedResource(): Bundle?

    fun getSubmittedResourceAsString(): String?

    fun setSubmittedResource(bundle: Bundle)

    fun setTheme(theme: Int)

    fun getTheme(): Int

    fun setCountry(country: String)

    fun getCountry(): String

    fun setStartAudit(startAudit: String)
    fun setEndAudit(endAudit: String)

    fun getStartAudit(): String

    fun getEndAudit(): String

    fun getCurrentIGVersion(): String

    fun setCurrentIGVersion(currentIGVersion: String)


    fun clear()

    fun clearAll()
}
