package com.argusoft.who.emcare.ui.home

import com.argusoft.who.emcare.data.remote.ApiResponse
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import kotlinx.coroutines.flow.flow
import org.hl7.fhir.r4.model.ActivityDefinition
import org.hl7.fhir.r4.model.Library
import org.hl7.fhir.r4.model.PlanDefinition
import org.hl7.fhir.r4.model.ValueSet
import javax.inject.Inject

class LibraryRepository @Inject constructor(
    private val fhirEngine: FhirEngine) {
    fun getLibraries() = flow {
        val list = fhirEngine.search<Library> {
        }
        emit(ApiResponse.Success(data = list))
    }

    fun getPlanDefinitions() = flow {
        val list = fhirEngine.search<PlanDefinition> {
        }
        emit(ApiResponse.Success(data = list))
    }

    fun getValueSets() = flow {
        val list = fhirEngine.search<ValueSet> {
        }
        emit(ApiResponse.Success(data = list))
    }

    fun getActivtityDefinitions() = flow {
        val list = fhirEngine.search<ActivityDefinition> {
        }
        emit(ApiResponse.Success(data = list))
    }
}