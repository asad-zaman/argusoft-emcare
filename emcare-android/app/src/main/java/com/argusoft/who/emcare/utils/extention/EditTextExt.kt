@file:Suppress("NOTHING_TO_INLINE", "UNUSED_PARAMETER")

package com.argusoft.who.emcare.utils.extention

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

fun EditText.getEnterText() = text.toString().trim()

fun EditText.isEmpty() = text.toString().trim().isEmpty()

inline fun EditText.onTextChanged(crossinline onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            onTextChanged(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    })
}