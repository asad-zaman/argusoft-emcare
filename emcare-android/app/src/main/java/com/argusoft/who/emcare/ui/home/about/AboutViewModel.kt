package com.argusoft.who.emcare.ui.home.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.DATE_FORMAT
import com.argusoft.who.emcare.ui.common.model.ActiveConsultationData
import com.argusoft.who.emcare.ui.common.model.PreviousConsultationData
import com.argusoft.who.emcare.ui.common.stageToBadgeMap
import com.argusoft.who.emcare.ui.common.stageToIconMap
import com.argusoft.who.emcare.ui.home.ConsultationFlowRepository
import com.argusoft.who.emcare.ui.home.patient.PatientRepository
import com.argusoft.who.emcare.utils.listener.SingleLiveEvent
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.search
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.PlanDefinition
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : ViewModel() {


    private val _bundleVersion = MutableLiveData<ApiResponse<String?>>()
    val bundleVersion: LiveData<ApiResponse<String?>> = _bundleVersion


    fun getBundleVersionNumber() {
        viewModelScope.launch {
            val planDefinitions = fhirEngine.search<PlanDefinition> {}
            if(planDefinitions.isNotEmpty())
                _bundleVersion.value = ApiResponse.Success(planDefinitions.last().version)
        }
    }
}