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

/**
 * ViewModel для главного экрана приложения.
 * Отвечает за получение и хранение версии приложения.
 * 
 * @param getVersionUseCase UseCase для получения версии приложения
 */
class MainViewModel(
    private val getVersionUseCase: GetAppVersionUseCase
) : ViewModel() {
    
    // Поток для хранения версии приложения
    private val _appVersion = MutableStateFlow(AppVersion("0.0", 0))
    val appVersion: StateFlow<AppVersion> = _appVersion.asStateFlow()
    
    /**
     * Загружает версию приложения.
     * Запускает корутину в области видимости ViewModel.
     * При успешной загрузке обновляет _appVersion.
     * При ошибке ничего не происходит — значение остаётся прежним.
     */
    fun loadAppVersion() {
        viewModelScope.launch {
            try {
                val version = getVersionUseCase()
                _appVersion.value = version
            } catch (e: Exception) {
                // Ошибка обработана, состояние не меняется
            }
        }
    }
    
    /**
     * Фабрика для создания MainViewModel с передачей зависимостей.
     * Используется для внедрения UseCase в ViewModel при создании через viewModel().
     */
    class Factory(private val getVersionUseCase: GetAppVersionUseCase) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainViewModel(getVersionUseCase) as T
        }
    }
}