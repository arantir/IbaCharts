package com.example.mychartsapp.data.repositories

import com.example.mychartsapp.data.datasources.VersionLocalDataSource
import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.repositories.VersionRepository

class VersionRepositoryImpl(
    private val localDataSource: VersionLocalDataSource
) : VersionRepository {
    override suspend fun getAppVersion(): AppVersion {
        val (versionName, versionCode) = localDataSource.getAppVersion()
        return AppVersion(versionName, versionCode)
    }
}