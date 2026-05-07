package com.example.mychartsapp.domain.usecases

import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.repositories.VersionRepository

/**
 * UseCase для получения версии приложения.
 * Реализует паттерн UseCase в Clean Architecture.
 *
 * @param repository Репозиторий для доступа к данным о версии
 */
class GetAppVersionUseCase(
    private val repository: VersionRepository
) {
    /**
     * Выполняет получение версии приложения.
     *
     * @return Объект AppVersion с данными о версии
     */
    suspend operator fun invoke(): AppVersion = repository.getAppVersion()
}