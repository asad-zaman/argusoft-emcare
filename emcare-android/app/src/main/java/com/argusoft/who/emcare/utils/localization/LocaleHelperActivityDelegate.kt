package com.argusoft.who.emcare.utils.localization

import android.app.Activity
import android.content.Context
import android.view.View
import java.util.*

interface LocaleHelperActivityDelegate {
    fun attachBaseContext(newBase: Context): Context
    fun onCreate(activity: Activity)
}

class LocaleHelperActivityDelegateImpl : LocaleHelperActivityDelegate {

    override fun onCreate(activity: Activity) {
        activity.window.decorView.layoutDirection =
            if (LocaleHelper.isRTL(Locale.getDefault())) View.LAYOUT_DIRECTION_RTL else View.LAYOUT_DIRECTION_LTR
    }

    override fun attachBaseContext(newBase: Context): Context {
        return LocaleHelper.onAttach(newBase)
    }

}

class LocaleHelperApplicationDelegate {
    fun attachBaseContext(base: Context): Context {
        return LocaleHelper.onAttach(base)
    }

    fun onConfigurationChanged(context: Context) {
        LocaleHelper.onAttach(context)
    }
}