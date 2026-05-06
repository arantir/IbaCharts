package com.example.mychartsapp.domain.usecases

import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.repositories.VersionRepository

class GetAppVersionUseCase(
    private val repository: VersionRepository
) {
    suspend operator fun invoke(): AppVersion = repository.getAppVersion()
}