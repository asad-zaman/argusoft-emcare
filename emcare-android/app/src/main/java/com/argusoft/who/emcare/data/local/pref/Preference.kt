package com.argusoft.who.emcare.data.local.pref

import com.argusoft.who.emcare.ui.common.model.LoggedInUser
import com.argusoft.who.emcare.ui.common.model.User

interface Preference {

    fun setLogin()

    fun isLogin(): Boolean

    fun setToken(token: String)

    fun getToken(): String

    fun setUser(user: User)

    fun getUser(): User?

    fun setLoggedInUser(loggedInUser: LoggedInUser)

    fun loggedInUser() : LoggedInUser?

    fun clear()
}
