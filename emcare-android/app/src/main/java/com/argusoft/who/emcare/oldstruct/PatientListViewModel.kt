package com.argusoft.who.emcare.oldstruct

import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.count
import com.google.android.fhir.search.search
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.Patient
import org.hl7.fhir.r4.model.RiskAssessment

/**
 * The ViewModel helper class for PatientItemRecyclerViewAdapter, that is responsible for preparing
 * data for UI.
 */
class PatientListViewModel(application: Application, private val fhirEngine: FhirEngine) :
  AndroidViewModel(application) {

  val liveSearchedPatients = MutableLiveData<List<PatientItem>>()
  val patientCount = liveData { emit(count()) }

  init {
    fetchAndPost { getSearchResults() }
  }

  fun searchPatientsByName(nameQuery: String) {
    fetchAndPost { getSearchResults(nameQuery) }
  }

  private fun fetchAndPost(search: suspend () -> List<PatientItem>) {
    viewModelScope.launch { liveSearchedPatients.value = search() }
  }

  private suspend fun count(): Long {
    return fhirEngine.count<Patient> { }
  }

  private suspend fun getSearchResults(nameQuery: String = ""): List<PatientItem> {
    val patients: MutableList<PatientItem> = mutableListOf()
    fhirEngine
      .search<Patient> {
        if (nameQuery.isNotEmpty())
          filter(
            Patient.NAME,
            {
              modifier = StringFilterModifier.CONTAINS
              value = nameQuery
            }
          )
        sort(Patient.GIVEN, Order.ASCENDING)
        count = 100
        from = 0
      }
      .mapIndexed { index, fhirPatient -> fhirPatient.toPatientItem(index + 1) }
      .let { patients.addAll(it) }

    val risks = getRiskAssessments()
    patients.forEach { patient ->
      risks["Patient/${patient.resourceId}"]?.let {
        patient.risk = it?.prediction?.first()?.qualitativeRisk?.coding?.first()?.code
        Log.d(TAG, "getSearchResults: ${patient.name} : ${patient.risk}")
      }
    }
    return patients
  }


  private suspend fun getRiskAssessments(): Map<String, RiskAssessment?> {
    return fhirEngine.search<RiskAssessment> {}.groupBy { it.subject.reference }.mapValues { entry
      ->
      entry
        .value
        .filter { it.hasOccurrence() }
        .sortedByDescending { it.occurrenceDateTimeType.value }
        .firstOrNull()
    }
  }

  /** The Patient's details for display purposes. */
  data class PatientItem(
    val id: String,
    val resourceId: String,
    val name: String,
    val gender: String,
    val dob: String,
    val identifier: String,
    val line: String,
    val city: String,
    val country: String,
    val isActive: Boolean,
    val html: String,
    var risk: String? = "",
    var riskItem: RiskAssessmentItem? = null
  ) {
    override fun toString(): String = name
  }

  /** The Observation's details for display purposes. */
  data class ObservationItem(
    val id: String,
    val code: String,
    val effective: String,
    val value: String
  ) {
    override fun toString(): String = code
  }

  data class ConditionItem(
    val id: String,
    val code: String,
    val effective: String,
    val value: String
  ) {
    override fun toString(): String = code
  }

  class PatientListViewModelFactory(
    private val application: Application,
    private val fhirEngine: FhirEngine
  ) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
      if (modelClass.isAssignableFrom(PatientListViewModel::class.java)) {
        return PatientListViewModel(application, fhirEngine) as T
      }
      throw IllegalArgumentException("Unknown ViewModel class")
    }
  }
}

internal fun Patient.toPatientItem(position: Int): PatientListViewModel.PatientItem {
  // Show nothing if no values available for gender and date of birth.
  val patientId = if (hasIdElement()) idElement.idPart else ""
  val name = if (hasName()) name[0].nameAsSingleString else ""
  val gender = if (hasGenderElement()) genderElement.valueAsString else ""
  val dob = if (hasBirthDateElement()) birthDateElement.valueAsString else ""
  val identifier = if (hasIdentifier()) identifier[0].value else ""
  val line = if(hasAddress() && address[0].line.isNotEmpty()) address[0].line[0].toString() else ""
  val city = if (hasAddress()) address[0].city else ""
  val country = if (hasAddress()) address[0].country else ""
  val isActive = active
  val html: String = if (hasText()) text.div.valueAsString else ""

  return PatientListViewModel.PatientItem(
    id = position.toString(),
    resourceId = patientId,
    name = name,
    gender = gender ?: "",
    dob = dob ?: "",
    identifier = identifier ?: "",
    line = line ?: "",
    city = city ?: "",
    country = country ?: "",
    isActive = isActive,
    html = html
  )
}