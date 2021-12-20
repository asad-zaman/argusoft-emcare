package com.argusoft.who.emcare.utils.extention

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Start activities with minimal boilerplate.
 * start<TestActivity>()
 * @receiver Context
 * @param configIntent [@kotlin.ExtensionFunctionType] Function1<Intent, Unit>
 */
inline fun <reified A : Activity> Context.start(configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(this, A::class.java).apply(configIntent))
}

inline fun <reified A : Activity> Fragment.start(configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(context, A::class.java).apply(configIntent))
}

/**
 * Start activities with minimal boilerplate.
 * startActivity(Intent.ACTION_VIEW)
 * @receiver Context
 * @param configIntent [@kotlin.ExtensionFunctionType] Function1<Intent, Unit>
 */
@Throws(ActivityNotFoundException::class)
inline fun Context.startActivity(action: String, configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(action).apply(configIntent))
}

@Throws(ActivityNotFoundException::class)
inline fun Fragment.startActivity(action: String, configIntent: Intent.() -> Unit = {}) {
    startActivity(Intent(action).apply(configIntent))
}

fun Context.getColor(@ColorRes colorRes: Int): Int {
    return ContextCompat.getColor(this, colorRes)
}

fun Context.showKeyboard(view: View) {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}