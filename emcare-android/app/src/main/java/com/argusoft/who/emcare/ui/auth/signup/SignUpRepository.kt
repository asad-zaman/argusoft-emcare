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

//    fun getFacilitiesAndRoles() = flow {
//        emit(Pair(api.getFacilities().whenSuccess {
//            database.saveFacilities(it)
//        }, api.getRoles()))
//    }

    fun getFacilities() = flow{
        emit(api.getFacilities().whenSuccess {
            database.saveFacilities(it)
        })
    }

    fun getCountries() = flow{
        emit(api.getCountries())
    }

    fun signUp(signupRequest: SignupRequest) = flow {
        emit(api.signup(signupRequest))
    }
}