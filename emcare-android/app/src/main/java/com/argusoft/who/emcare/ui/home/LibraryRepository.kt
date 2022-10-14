package com.argusoft.who.emcare.ui.home

import android.app.Application
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.LOCATION_EXTENSION_URL
import com.argusoft.who.emcare.utils.extention.toPatientItem
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Operation
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.StringFilterModifier
import com.google.android.fhir.search.search
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.Identifier
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.Patient
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val fhirEngine: FhirEngine,
    private val application: Application,
    private val consultationFlowRepository: ConsultationFlowRepository,
) {
    fun getLibraries() = flow {
        val list = fhirEngine.search<Library> {
        }
        emit(ApiResponse.Success(data = list))
    }
}