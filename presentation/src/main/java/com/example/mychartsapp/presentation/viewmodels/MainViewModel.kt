package com.example.mychartsapp.presentation.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.usecases.GetAppVersionUseCase
import kotlinx.coroutines.launch

/**
 * ViewModel для главного экрана приложения.
 * Отвечает за загрузку и предоставление данных о версии приложения.
 *
 * @param getAppVersionUseCase UseCase для получения версии приложения
 */
class MainViewModel(
    private val getAppVersionUseCase: GetAppVersionUseCase
) : ViewModel() {
    
    /** LiveData с версией приложения */
    private val _appVersion = MutableLiveData<AppVersion>()
    val appVersion: LiveData<AppVersion> = _appVersion
    
    /** LiveData с состоянием загрузки */
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    /** LiveData с сообщением об ошибке */
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error
    
    /**
     * Загружает версию приложения.
     * Обновляет состояние загрузки и при необходимости - ошибку.
     */
    fun loadAppVersion() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val version = getAppVersionUseCase()
                _appVersion.value = version
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message ?: "Unknown error"
            } finally {
                _isLoading.value = false
            }
        }
    }
}