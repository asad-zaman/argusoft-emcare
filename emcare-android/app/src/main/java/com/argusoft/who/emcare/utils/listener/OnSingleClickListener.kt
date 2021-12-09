package com.argusoft.who.emcare.utils.listener

import android.os.SystemClock
import android.view.View

abstract class OnSingleClickListener : View.OnClickListener {

    private var lastClickTime: Long = 0

    override fun onClick(view: View) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 1000) {
            return
        }
        lastClickTime = SystemClock.elapsedRealtime()
        onSingleClick(view)
    }

    abstract fun onSingleClick(view: View)
}