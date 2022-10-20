package com.argusoft.who.emcare.ui.home.patient.profile

import androidx.lifecycle.ViewModel
import com.argusoft.who.emcare.ui.common.model.ActiveConsultationData
import com.argusoft.who.emcare.ui.common.model.PreviousConsultationData
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PatientProfileViewModel @Inject constructor(
) : ViewModel() {

    fun getActiveConsultations() : ArrayList<ActiveConsultationData?>{
        return arrayListOf(
            ActiveConsultationData("Sick Child, First Visit", "06/04/22"),
            ActiveConsultationData("Sick Child, First Visit", "06/04/22"),
        )
    }

    fun getPreviousConsultations() : ArrayList<PreviousConsultationData?>{
        return arrayListOf(
            PreviousConsultationData("Sick Child, Follow Up", "06/04/22"),
            PreviousConsultationData("Sick Child, Follow Up", "01/04/22"),
            PreviousConsultationData("Sick Child, Follow Up", "26/03/22"),
            PreviousConsultationData("Well Child Clinic", "18/03/22"),
            PreviousConsultationData("Sick Child, Follow Up", "11/03/22"),
            PreviousConsultationData("Sick Child, Follow Up", "07/01/22"),
        )
    }

}