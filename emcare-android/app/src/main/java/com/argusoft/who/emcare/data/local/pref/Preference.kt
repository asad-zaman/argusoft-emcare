package com.argusoft.who.emcare.data.local.pref

interface Preference {

    fun setLogin()

    fun isLogin(): Boolean

    fun clear()
}
