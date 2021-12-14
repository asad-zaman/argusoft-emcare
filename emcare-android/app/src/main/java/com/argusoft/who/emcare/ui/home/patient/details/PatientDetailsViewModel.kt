package com.argusoft.who.emcare.ui.home.patient.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.R
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.common.model.PatientItemData
import com.argusoft.who.emcare.utils.extention.navigate
import com.google.android.fhir.FhirEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import javax.inject.Inject

@HiltViewModel
class PatientDetailsViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : ViewModel() {


    private val _patientItem = MutableLiveData<PatientItem>()
    val patientItem : LiveData<PatientItem> = _patientItem



    fun getPatientDetails(patientId: String?) {
        viewModelScope.launch {
            if(patientId != null) {
                _patientItem.value = convertPatientToPatientItem(fhirEngine.load(Patient::class.java, patientId))
            }
        }
    }

    fun deletePatient(patientId: String?) {
        viewModelScope.launch {
            if(patientId != null) {
                fhirEngine.remove(Patient::class.java, patientId)
            }
        }
    }

    fun createPatientItemDataListFromPatientItem(patientItem: PatientItem) : List<PatientItemData> {
        val patientItemDataList = mutableListOf<PatientItemData>()

        patientItemDataList.add(PatientItemData("Identifier",patientItem.identifier))
        patientItemDataList.add(PatientItemData("Gender",patientItem.gender))
        patientItemDataList.add(PatientItemData("Date Of Birth",patientItem.dob))
        patientItemDataList.add(PatientItemData("Address","${patientItem.line}, ${patientItem.city}, ${patientItem.country} "))

        return patientItemDataList
    }

    private fun convertPatientToPatientItem(patient: Patient) : PatientItem {

        val patientId = if (patient.hasIdElement()) patient.idElement.idPart else ""
        val name = if (patient.hasName()) patient.name[0].nameAsSingleString else ""
        val gender = if (patient.hasGenderElement()) patient.genderElement.valueAsString else ""
        val dob = if (patient.hasBirthDateElement()) patient.birthDateElement.valueAsString else ""
        val identifier = if (patient.hasIdentifier()) patient.identifier[0].value else ""
        val line = if(patient.hasAddress() && patient.address[0].line.isNotEmpty()) patient.address[0].line[0].toString() else ""
        val city = if (patient.hasAddress()) patient.address[0].city else ""
        val country = if (patient.hasAddress()) patient.address[0].country else ""
        val isActive = patient.active
        val html: String = if (patient.hasText()) patient.text.div.valueAsString else ""

        return PatientItem(
            id = patientId,
            name = name,
            gender = gender,
            dob = dob,
            identifier = identifier,
            line = line,
            city = city,
            country = country,
            isActive = isActive,
            html = html
        )

    }
}