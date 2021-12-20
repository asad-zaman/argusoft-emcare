package com.argusoft.who.emcare.utils.extention

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
import android.text.TextUtils
import android.text.method.LinkMovementMethod
import android.text.style.*
import android.util.Patterns
import android.widget.TextView
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import com.argusoft.who.emcare.utils.common.CustomTypefaceSpan


/**
 * USAGE :
 * Created by
 */

fun String.isValidURL() =
    (!TextUtils.isEmpty(this) && Patterns.WEB_URL.matcher(this).matches())

fun String.isValidEmail() =
    (!TextUtils.isEmpty(this) && Patterns.EMAIL_ADDRESS.matcher(this).matches())

fun String.isValidPhone() =
    (!TextUtils.isEmpty(this) && Patterns.PHONE.matcher(this).matches())

inline fun String?.isNotEmpty(value: (String) -> Unit) {
    if (!isNullOrEmpty()) value(this)
}

inline fun String?.orEmpty(value: () -> String): String {
    return if (isNullOrEmpty()) value.invoke()
    else this
}

fun buildString(action: StringBuilder.() -> Unit): String {
    return StringBuilder().action().toString()
}

fun spannable(func: () -> SpannableString) = func()

fun span(s: CharSequence, o: Any) =
    (if (s is String) SpannableString(s) else s as? SpannableString
        ?: SpannableString(""))
        .apply {
            setSpan(o, 0, length, SPAN_EXCLUSIVE_EXCLUSIVE)
        }

infix fun SpannableString.concat(s: CharSequence): SpannableStringBuilder = SpannableStringBuilder().append(this).append(s)

operator fun SpannableString.plus(s: SpannableString) = SpannableString(this concat s)
operator fun SpannableString.plus(s: String) = SpannableString(this concat s)

fun bold(s: CharSequence) = span(s, StyleSpan(Typeface.BOLD))
fun italic(s: CharSequence) = span(s, StyleSpan(Typeface.ITALIC))
fun sub(s: CharSequence) = span(s, SubscriptSpan()) // baseline is lowered
fun size(size: Float, s: CharSequence) = span(s, RelativeSizeSpan(size))
fun getColor(color: Int, s: CharSequence) = span(s, ForegroundColorSpan(color))

fun url(url: String, s: CharSequence) = span(s, URLSpan(url))

/**
 * Used to apply Click event on Provided @param url.
 */
fun TextView.url(url: String, s: CharSequence): SpannableString = run {
    val span = span(s, URLSpan(url))
    this.isSelected = true
    movementMethod = LinkMovementMethod.getInstance()
    return span
}

/**
 * For apply custom font name in spannable
 */
fun customFont(fontName: String, text: String): SpannableString {
    val font = TypefaceSpan(fontName)
    return span(s = text, o = font)
}

/**
 * For apply custom font in spannable
 *
 * @param font Font res id, which needs to be applied to SpannableString
 * @return
 */
fun customFont(context: Context, @FontRes font: Int, text: String): SpannableString {
    val typeface: Typeface? = ResourcesCompat.getFont(context, font)
    return typeface?.let {
        val co = CustomTypefaceSpan(typeface)
        span(s = text, o = co)
    } ?: SpannableString(text)
}