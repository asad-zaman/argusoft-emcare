package com.argusoft.who.emcare.ui.common.base

import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    abstract fun clearLiveData()
}