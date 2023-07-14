package com.argusoft.who.emcare.sync

import com.argusoft.who.emcare.data.local.pref.Preference
import com.google.android.fhir.sync.HttpAuthenticator
import com.google.android.fhir.sync.HttpAuthenticationMethod
import javax.inject.Inject


class EmcareAuthenticator @Inject constructor(
    private val preference: Preference
) : HttpAuthenticator {

    override fun getAuthenticationMethod() : HttpAuthenticationMethod {
        return HttpAuthenticationMethod.Bearer(preference.getToken())
    }
}