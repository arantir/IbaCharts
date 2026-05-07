package com.example.mychartsapp.domain.models

/**
 * Модель версии приложения.
 * Используется в domain слое для передачи данных о версии.
 *
 * @property versionName Название версии (например, "1.004")
 * @property versionCode Код версии (целое число, увеличивается с каждой сборкой)
 */
data class AppVersion(
    val versionName: String,
    val versionCode: Long
)