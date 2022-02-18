package com.argusoft.who.emcare.ui.home.settings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.argusoft.who.emcare.data.remote.ApiResponse
import com.argusoft.who.emcare.ui.common.model.Language
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val languageRepository: LanguageRepository
) : ViewModel() {

    private val _languagesApiState = MutableLiveData<ApiResponse<List<Language>>>()
    private val _languageApiState = MutableLiveData<ApiResponse<Language>>()
    val languagesApiState: LiveData<ApiResponse<List<Language>>> = _languagesApiState
    val languageApiState: LiveData<ApiResponse<Language>> = _languageApiState

    var languageId: Int? = null

    init {
        getAllLanguages()
    }

    private fun getAllLanguages() {
        _languagesApiState.value = ApiResponse.Loading()
        viewModelScope.launch {
            languageRepository.getAllLanguages().collect {
                _languagesApiState.value = it
            }
        }
    }

    fun getLanguageByCode(languageCode: String) {
        _languageApiState.value = ApiResponse.Loading()
        viewModelScope.launch {
            languageRepository.getLanguageByCode(languageCode).collect {
                _languageApiState.value = it
            }
        }
    }
}