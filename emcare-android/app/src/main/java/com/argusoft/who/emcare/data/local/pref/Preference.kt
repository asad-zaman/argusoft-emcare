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

    fun setLoggedInUser(loggedInUser: LoggedInUser)

    fun getLoggedInUser() : LoggedInUser?

    fun writeLastSyncTimestamp(timestamp: String)

    fun getSubmittedResource(): Bundle?

    fun getSubmittedResourceAsString(): String?

    fun setSubmittedResource(bundle: Bundle)

    fun clear()
}
