package com.argusoft.who.emcare.utils.listener

import android.text.Editable
import android.text.TextWatcher

interface OnTextChanged : TextWatcher {

    override fun afterTextChanged(editable: Editable?) {
        editable?.let { onTextChanged(it) }
    }

    override fun beforeTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //Empty Block
    }

    override fun onTextChanged(charSequence: CharSequence?, p1: Int, p2: Int, p3: Int) {
        //Empty Block
    }

    fun onTextChanged(editable: Editable)
}