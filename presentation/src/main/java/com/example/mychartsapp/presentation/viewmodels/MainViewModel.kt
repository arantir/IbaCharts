package com.example.mychartsapp.presentation.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.usecases.GetAppVersionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val getVersionUseCase: GetAppVersionUseCase
) : ViewModel() {
    
    private val _appVersion = MutableStateFlow(AppVersion("0.0", 0))
    val appVersion: StateFlow<AppVersion> = _appVersion.asStateFlow()
    
    fun loadAppVersion() {
        viewModelScope.launch {
            val version = getVersionUseCase()
            _appVersion.value = version
        }
    }
    
    class Factory(private val getVersionUseCase: GetAppVersionUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(getVersionUseCase) as T
        }
    }
}