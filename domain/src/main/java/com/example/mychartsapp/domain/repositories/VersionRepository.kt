package com.example.mychartsapp.domain.repositories

import com.example.mychartsapp.domain.models.AppVersion

/**
 * Репозиторий для получения версии приложения.
 * Абстракция для доступа к данным о версии из любого источника.
 */
interface VersionRepository {
    
    /**
     * Получает информацию о версии приложения.
     *
     * @return Объект AppVersion с данными о версии
     */
    suspend fun getAppVersion(): AppVersion
}