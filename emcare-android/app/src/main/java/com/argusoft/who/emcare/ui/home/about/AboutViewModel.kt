package com.argusoft.who.emcare.ui.home.about

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.google.android.fhir.FhirEngine
import com.google.android.fhir.search.Order
import com.google.android.fhir.search.search
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.hl7.fhir.r4.model.PlanDefinition
import javax.inject.Inject

@HiltViewModel
class AboutViewModel @Inject constructor(
    private val fhirEngine: FhirEngine
) : ViewModel() {


    private val _bundleVersion = MutableLiveData<ApiResponse<String?>>()
    val bundleVersion: LiveData<ApiResponse<String?>> = _bundleVersion

    //Stores the version of latest planDefinition as the IG Bundle Version.
    fun getBundleVersionNumber() {
        viewModelScope.launch {
            val planDefinitions = fhirEngine.search<PlanDefinition> {
                sort(PlanDefinition.DATE, Order.ASCENDING)
            }
            if(planDefinitions.isNotEmpty())
                _bundleVersion.value = ApiResponse.Success(planDefinitions.last().version)
        }
    }
}