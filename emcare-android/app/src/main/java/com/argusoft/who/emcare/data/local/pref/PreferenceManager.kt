package com.argusoft.who.emcare.data.local.pref

import com.argusoft.who.emcare.ui.common.model.User
import com.argusoft.who.emcare.utils.extention.fromJson
import com.argusoft.who.emcare.utils.extention.toJson

class PreferenceManager(private val sharedPreferences: EncPref) : Preference {

    companion object {
        private const val IS_LOGIN = "pref_is_login"
        private const val USER = "USER"
        private const val TOKEN = "TOKEN"
    }

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
        return sharedPreferences.getString(TOKEN)
    }

    override fun setUser(user: User) {
        sharedPreferences.putString(USER, user.toJson())
    }

    override fun getUser(): User? {
        return sharedPreferences.getString(USER, "{}").fromJson<User>()
    }

    override fun clear() {
        sharedPreferences.clear()
    }
}
