package com.argusoft.who.emcare.ui.home.patient.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.PatientItem
import com.argusoft.who.emcare.ui.common.model.PatientItemData
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.google.android.fhir.FhirEngine
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import javax.inject.Inject

@HiltViewModel
class PatientDetailsViewModel @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val patientRepository: PatientRepository
) : ViewModel() {


    private val _patientItem = MutableLiveData<ApiResponse<PatientItem>>()
    val patientItem: LiveData<ApiResponse<PatientItem>> = _patientItem

    private val _deletePatientSuccessState = MutableLiveData<Int>()
    val deletePatientSuccessState: LiveData<Int> = _deletePatientSuccessState

    private val _deletePatientLoadingState = MutableLiveData<ApiResponse<Patient>>()
    val deletePatientLoadingState: LiveData<ApiResponse<Patient>> = _deletePatientLoadingState


    fun getPatientDetails(patientId: String?) {
        _patientItem.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.getPatientDetails(patientId).collect {
                _patientItem.value = it
            }
        }
    }

    fun deletePatient(patientId: String?) {
        _deletePatientLoadingState.value = ApiResponse.Loading()
        viewModelScope.launch {
            patientRepository.deletePatient(patientId)
        }
    }

    fun createPatientItemDataListFromPatientItem(patientItem: PatientItem?): List<PatientItemData> {
        val patientItemDataList = mutableListOf<PatientItemData>()

        patientItemDataList.add(PatientItemData("Identifier", patientItem?.identifier))
        patientItemDataList.add(PatientItemData("Gender", patientItem?.gender))
        patientItemDataList.add(PatientItemData("Date Of Birth", patientItem?.dob))
        patientItemDataList.add(PatientItemData("Address", "${patientItem?.line}, ${patientItem?.city}, ${patientItem?.country} "))

        return patientItemDataList
    }
}