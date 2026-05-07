package com.example.mychartsapp.data.datasources

import android.content.Context
import android.content.pm.PackageManager

/**
 * Источник данных для получения версии приложения из системы Android.
 * Работает с PackageManager для извлечения информации о текущем приложении.
 *
 * @param context Контекст приложения для доступа к PackageManager
 */
class VersionLocalDataSource(
    private val context: Context
) {
    /**
     * Получает версию приложения из системного PackageManager.
     *
     * @return Пара (versionName, versionCode).
     *         В случае ошибки возвращает ("error", 0L)
     */
    fun getAppVersion(): Pair<String, Long> {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "unknown"
            
            // Для Android P и выше используем longVersionCode
            val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
            Pair(versionName, versionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            // Возвращаем значения по умолчанию при ошибке
            Pair("error", 0L)
        }
    }
}