@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package com.argusoft.who.emcare.utils.extention

import android.content.Context
import android.net.ConnectivityManager
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.argusoft.who.emcare.R
import com.google.android.material.snackbar.Snackbar
import java.lang.ref.WeakReference


/**
 * USAGE : Alert utils class for basic operations like display Alert.
 * Created by
 */
var snackBarWeekReference: WeakReference<Snackbar?>? = null

fun AppCompatActivity?.dismissSnackBar() {
    takeIf { it != null }?.let {
        snackBarWeekReference!!.get()!!.dismiss()
    }
}

private fun Context?.getSnackBarWeekReference(
    view: View,
    message: String = this!!.getString(R.string.alert_message_error),
    duration: Int = Snackbar.LENGTH_LONG
) {
    snackBarWeekReference = WeakReference(this?.let {
        Snackbar.make(
            view,
            message,
            duration
        )
    })
}

fun Context?.isInternetAvailable(): Boolean {
    val connectivityManager =
        this?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val activeNetworkInfo = connectivityManager.activeNetworkInfo
    return activeNetworkInfo != null && activeNetworkInfo.isConnected
}

fun Context?.showToast(
    @StringRes messageResId: Int? = null,
    message: CharSequence? = null,
    duration: Int = Toast.LENGTH_LONG
) {
    if (messageResId == null && message != null)
        Toast.makeText(this, message, duration).show()
    else if (messageResId != null && message == null)
        Toast.makeText(this, messageResId, duration).show()
}

//
//fun Context?.showSnackBar(
//    message: String = this!!.getString(R.string.alert_message_error),
//    duration: Int = Snackbar.LENGTH_LONG
//) {
//    val context = this as AppCompatActivity
//    context.showSnackBar(context.findViewById(android.R.id.content)!!, message, duration, false)
//}

fun Context?.showSnackBar(
    view: View,
    message: String = this!!.getString(R.string.alert_message_error),
    duration: Int = Snackbar.LENGTH_LONG
) {
    takeIf { it != null }?.let {
        getSnackBarWeekReference(view, message, duration)
        snackBarWeekReference!!.get()!!.show()
    }
}

fun Fragment?.showSnackBar(
    view: View,
    message: String = this!!.getString(R.string.alert_message_error),
    duration: Int = Snackbar.LENGTH_LONG
) {
    takeIf { it != null }?.let {
        this?.activity?.getSnackBarWeekReference(view, message, duration)
        snackBarWeekReference!!.get()!!.show()
    }
}

fun Context?.showSnackBar(
    view: View,
    message: String? = this?.getString(R.string.alert_message_error),
    messageResId: Int? = R.string.alert_message_error,
    duration: Int = Snackbar.LENGTH_SHORT,
    isError: Boolean,
    @StringRes action: Int? = null,
    undoClickListener: View.OnClickListener? = null
) {
    takeIf { it != null }?.let {
        val msg = message ?: messageResId?.let { this?.getString(it) } ?: this?.getString(R.string.alert_message_error) ?: ""
        getSnackBarWeekReference(view, msg, if (undoClickListener == null) duration else Snackbar.LENGTH_INDEFINITE)
        val snackBar = snackBarWeekReference!!.get()
        val snackBarView = snackBar?.view?.apply {
            /*val params = layoutParams as FrameLayout.LayoutParams
            params.gravity = Gravity.TOP
            layoutParams = params*/
        }
        val textView =
            snackBarView?.findViewById(com.google.android.material.R.id.snackbar_text) as TextView

        textView.let {
            if (isError) {
                snackBarView.setBackgroundColor(it.getColor(R.color.colorSnackBarErrorBg))
                textView.setTextColor(it.getColor(R.color.colorSnackBarErrorText))
            } else {
                snackBarView.setBackgroundColor(it.getColor(R.color.colorSnackBarSuccessBg))
                textView.setTextColor(it.getColor(R.color.colorSnackBarSuccessText))
            }
        }

        action?.let {
            undoClickListener?.let {
                snackBar.setAction(action, undoClickListener)
                snackBar.setActionTextColor(snackBar.view.getColor(R.color.colorWhite))
            }
        }
        textView.maxLines = 5
        snackBar.show()
    }
}

/**
 * Shows the SnackBar inside an Activity or Fragment
 *
 * @param messageRes Text to be shown inside the SnackBar
 * @param duration Duration of the SnackBar
 * @param f Action of the SnackBar
 */
fun View.showSnackBar(@StringRes messageRes: Int, duration: Int = Snackbar.LENGTH_LONG, f: Snackbar.() -> Unit) {
    context.getSnackBarWeekReference(this, resources.getString(messageRes), duration)
    val snackBar = snackBarWeekReference!!.get()
    snackBar!!.f()
    snackBar.show()
}

/**
 * Adds action to the SnackBar
 *
 * @param actionRes Action text to be shown inside the SnackBar
 * @param color Color of the action text
 * @param listener Onclick listener for the action
 */
fun Snackbar.action(@StringRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) {
    setAction(actionRes, listener)
    color?.let { setActionTextColor(color) }
}