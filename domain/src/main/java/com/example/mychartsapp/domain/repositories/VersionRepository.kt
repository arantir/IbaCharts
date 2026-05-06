package com.example.mychartsapp.domain.repositories

import com.example.mychartsapp.domain.models.AppVersion

interface VersionRepository {
    suspend fun getAppVersion(): AppVersion
}