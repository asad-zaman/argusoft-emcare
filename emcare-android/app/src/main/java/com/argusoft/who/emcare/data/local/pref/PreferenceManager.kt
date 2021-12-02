package com.argusoft.who.emcare.data.local.pref

class PreferenceManager(private val sharedPreferences: EncPref) : Preference {

    companion object {
        private const val IS_LOGIN = "pref_is_login"
    }

    override fun setLogin() {
        sharedPreferences.putBoolean(IS_LOGIN, true)
    }

    override fun isLogin(): Boolean {
        return sharedPreferences.getBoolean(IS_LOGIN, false)
    }

    override fun clear() {
        sharedPreferences.clear()
    }
}
