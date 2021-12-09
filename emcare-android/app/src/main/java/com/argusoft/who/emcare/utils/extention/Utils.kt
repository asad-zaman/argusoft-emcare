@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package com.argusoft.who.emcare.utils.extention

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.SparseBooleanArray
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import timber.log.Timber
import java.io.File

inline fun <T : Fragment> T.withArgs(argsBuilder: Bundle.() -> Unit): T = this.apply {
    arguments = Bundle().apply(argsBuilder)
}

fun Any?.timber() {
    Timber.e("$this")
}

fun Activity.hideKeyboard(view: View?) {
    val inputMethodManager =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    takeIf { view != null }?.let {
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    } ?: let {
        inputMethodManager.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}

fun Fragment.hideKeyboard(v: View? = view) {
    val inputMethodManager =
        activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(v?.windowToken, 0)
}

fun View?.navigate(navId: Int, argsBuilder: Bundle.() -> Unit = {}) {
    this?.findNavController()?.navigate(navId, Bundle().apply(argsBuilder))
}

fun Fragment?.navigate(navId: Int, argsBuilder: Bundle.() -> Unit = {}) {
    this?.findNavController()?.navigate(navId, Bundle().apply(argsBuilder))
}

fun DialogFragment?.navigate(navId: Int, argsBuilder: Bundle.() -> Unit = {}) {
    this?.findNavController()?.navigate(navId, Bundle().apply(argsBuilder))
}

inline fun <reified T> Collection<T>?.toArrayList(): ArrayList<T> {
    return (this ?: arrayListOf()).toCollection(ArrayList())
}

inline fun <reified T> T.toJson(): String {
    return Moshi.Builder().build().adapter(T::class.java).toJson(this)
}

inline fun <reified T> String.fromJson(): T? {
    return Moshi.Builder().build().adapter(T::class.java).fromJson(this)
}

inline fun <reified T> String.fromJsonArray(): List<T>? {
    val type = Types.newParameterizedType(List::class.java, T::class.java)
    val adapter = Moshi.Builder().build().adapter<List<T>>(type)
    return adapter.fromJson(this)
}

fun String.toRequestBody(): RequestBody {
    return this.toRequestBody("text".toMediaTypeOrNull())
}

fun File.toRequestBody(): RequestBody {
    return this.asRequestBody("image".toMediaTypeOrNull())
}

fun String.toMultipartBody(key: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        key, this
    )
}

fun <E> Collection<E>?.ifIsNotEmpty(callbacks: (List<E>) -> Unit) {
    if (!isNullOrEmpty()) {
        callbacks(this.toList())
    }
}

fun Int?.get(): Int {
    return this ?: -1
}

fun List<Int>?.toSparsBooleanArray(): SparseBooleanArray {
    return SparseBooleanArray().apply {
        this@toSparsBooleanArray?.forEach {
            put(it, true)
        }
    }
}

