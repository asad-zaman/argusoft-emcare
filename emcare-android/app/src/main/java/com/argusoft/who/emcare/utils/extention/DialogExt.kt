@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package com.argusoft.who.emcare.utils.extention

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import com.argusoft.who.emcare.R
import java.lang.ref.WeakReference

var dialogWeekReference: WeakReference<Dialog?>? = null

fun Context?.showProgressDialog() {
    dialogWeekReference = WeakReference(dialogWeekReference?.get() ?: this?.let { Dialog(it) })
    val dialog = dialogWeekReference?.get()
    dialog?.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
    dialog?.setTitle(null)
    dialog?.setCancelable(false)
    dialog?.setOnCancelListener(null)
    dialog?.addContentView(
        ProgressBar(this),
        ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    )
    dialog?.show()
}

fun dismissProgressDialog() {
    dialogWeekReference?.get()?.takeIf { it.isShowing }?.dismiss()
}

fun Context?.showAlertDialog(
    @StringRes messageResId: Int,
    @StringRes titleResId: Int = R.string.app_name,
    isCancelable: Boolean = false,
    @StringRes positiveButtonResId: Int = R.string.button_ok,
    positiveClickListener: DialogInterface.OnClickListener? = null,
    @StringRes negativeButtonResId: Int? = null,
    negativeClickListener: DialogInterface.OnClickListener? = null,
    @StringRes neutralButtonResId: Int? = null,
    neutralClickListener: DialogInterface.OnClickListener? = null
) {
    this?.also { context ->
        val builder = AlertDialog.Builder(context)
            .setMessage(messageResId)
        builder.setTitle(titleResId)
        positiveButtonResId.let { positiveButtonResId ->
            builder.setPositiveButton(positiveButtonResId) { dialogInterface, i ->
                positiveClickListener.takeIf { it != null }?.onClick(dialogInterface, i)
            }
        }
        negativeButtonResId?.let { negativeButtonResId ->
            builder.setNegativeButton(negativeButtonResId) { dialogInterface, i ->
                negativeClickListener.takeIf { it != null }?.onClick(dialogInterface, i)
                    ?: dialogInterface.dismiss()
            }
        }
        neutralButtonResId?.let { neutralButtonResId ->
            builder.setNeutralButton(neutralButtonResId) { dialogInterface, i ->
                neutralClickListener.takeIf { it != null }?.onClick(dialogInterface, i)
                    ?: dialogInterface.dismiss()
            }
        }
        val alertDialog = builder.create()
        alertDialog.setCancelable(isCancelable)
        alertDialog.setCanceledOnTouchOutside(isCancelable)
        builder.show()
    }
}

/************************* Alert Dialog **********************************/
/**
 *  alertDialog {
 *      setMessage(R.string.dialog_msg_confirm_irreversible_stuff)
 *      okButton { irreversibleStuff() }
 *      cancelButton()
 *  }.onShow {
 *      positiveButton.textColorResource = R.color.red_500
 *  }.show()
 * @receiver Context
 * @param dialogConfig [@kotlin.ExtensionFunctionType] Function1<Builder, Unit>
 * @return AlertDialog
 */
inline fun Context.alertDialog(dialogConfig: AlertDialog.Builder.() -> Unit): AlertDialog {
    return AlertDialog.Builder(this)
        .apply(dialogConfig)
        .create()
}

inline fun AlertDialog.onShow(crossinline onShowListener: AlertDialog.() -> Unit) = apply {
    setOnShowListener { onShowListener() }
}

@RequiresApi(17)
inline fun AlertDialog.Builder.onDismiss(crossinline handler: (dialog: DialogInterface) -> Unit) {
    setOnDismissListener { handler(it) }
}

inline val AlertDialog.positiveButton: Button
    get() = requireNotNull(getButton(AlertDialog.BUTTON_POSITIVE)) {
        "The dialog has no positive button or has not been shown yet."
    }

inline val AlertDialog.neutralButton: Button
    get() = requireNotNull(getButton(AlertDialog.BUTTON_NEUTRAL)) {
        "The dialog has no neutral button or has not been shown yet."
    }

inline val AlertDialog.negativeButton: Button
    get() = requireNotNull(getButton(AlertDialog.BUTTON_NEGATIVE)) {
        "The dialog has no negative button or has not been shown yet."
    }

inline fun AlertDialog.Builder.okButton(crossinline handler: (dialog: DialogInterface) -> Unit) {
    setPositiveButton(android.R.string.ok) { dialog: DialogInterface, _: Int -> handler(dialog) }
}

@Suppress("NOTHING_TO_INLINE")
inline fun AlertDialog.Builder.okButton() {
    setPositiveButton(android.R.string.ok, null)
}

inline fun AlertDialog.Builder.cancelButton(crossinline handler: (dialog: DialogInterface) -> Unit) {
    setNegativeButton(android.R.string.cancel) { dialog: DialogInterface, _: Int -> handler(dialog) }
}

@Suppress("NOTHING_TO_INLINE")
inline fun AlertDialog.Builder.cancelButton() {
    setNegativeButton(android.R.string.cancel, null)
}

inline fun AlertDialog.Builder.positiveButton(
    @StringRes textResId: Int,
    crossinline handler: (dialog: DialogInterface) -> Unit
) {
    setPositiveButton(textResId) { dialog: DialogInterface, _: Int -> handler(dialog) }
}

inline fun AlertDialog.Builder.neutralButton(
    @StringRes textResId: Int,
    crossinline handler: (dialog: DialogInterface) -> Unit
) {
    setNeutralButton(textResId) { dialog: DialogInterface, _: Int -> handler(dialog) }
}

inline fun AlertDialog.Builder.negativeButton(
    @StringRes textResId: Int,
    crossinline handler: (dialog: DialogInterface) -> Unit
) {
    setNegativeButton(textResId) { dialog: DialogInterface, _: Int -> handler(dialog) }
}
