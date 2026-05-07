package com.example.mychartsapp.data.repositories

import com.example.mychartsapp.data.datasources.VersionLocalDataSource
import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.repositories.VersionRepository

/**
 * Реализация репозитория для работы с версией приложения.
 * Использует локальный источник данных для получения информации о версии.
 *
 * @param localDataSource Источник данных для получения версии из системы Android
 */
class VersionRepositoryImpl(
    private val localDataSource: VersionLocalDataSource
) : VersionRepository {
    
    /**
     * Получает версию приложения из локального источника.
     *
     * @return Объект AppVersion с данными о версии
     */
    override suspend fun getAppVersion(): AppVersion {
        val (versionName, versionCode) = localDataSource.getAppVersion()
        return AppVersion(versionName, versionCode)
    }
}