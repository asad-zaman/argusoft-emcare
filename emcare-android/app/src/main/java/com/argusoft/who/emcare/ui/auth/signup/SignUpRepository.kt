package com.argusoft.who.emcare.ui.auth.signup

import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.remote.Api
import com.argusoft.who.emcare.ui.common.model.SignupRequest
import com.argusoft.who.emcare.utils.extention.whenSuccess
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class SignUpRepository @Inject constructor(
    private val api: Api,
    private val database: Database
) {

    fun getLocationsAndRoles() = flow {
        emit(Pair(api.getLocations().whenSuccess {
            database.saveLocations(it)
        }, api.getRoles()))
    }

    fun signUp(signupRequest: SignupRequest) = flow {
        emit(api.signup(signupRequest))
    }
}