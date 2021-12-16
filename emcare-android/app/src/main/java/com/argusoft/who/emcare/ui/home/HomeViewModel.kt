package com.argusoft.who.emcare.ui.home

import androidx.lifecycle.ViewModel
import com.argusoft.who.emcare.data.local.pref.Preference
import com.argusoft.who.emcare.data.remote.Api
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val api: Api,
    private val preference: Preference
) : ViewModel() {

}