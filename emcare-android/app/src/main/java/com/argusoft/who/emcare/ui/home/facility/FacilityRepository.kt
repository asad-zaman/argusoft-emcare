package com.argusoft.who.emcare.ui.home.facility

import com.argusoft.who.emcare.data.local.database.Database
import com.argusoft.who.emcare.data.local.pref.Preference
import javax.inject.Inject

class FacilityRepository @Inject constructor(
    private val database: Database,
    private val preference: Preference
) {

    fun getFacilities() = run { preference.getLoggedInUser()?.facility }

}