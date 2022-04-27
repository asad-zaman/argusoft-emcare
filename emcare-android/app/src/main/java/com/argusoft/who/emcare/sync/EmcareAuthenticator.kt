package com.argusoft.who.emcare.sync

import com.argusoft.who.emcare.data.local.pref.Preference
import com.google.android.fhir.sync.Authenticator
import javax.inject.Inject


class EmcareAuthenticator @Inject constructor(
    private val preference: Preference
) : Authenticator {
    override fun getAccessToken(): String {
        return preference.getToken()
    }
}