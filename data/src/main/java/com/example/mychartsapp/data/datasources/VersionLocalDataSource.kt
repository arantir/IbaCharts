package com.example.mychartsapp.data.datasources

import android.content.Context
import android.content.pm.PackageManager

class VersionLocalDataSource(
    private val context: Context
) {
    fun getAppVersion(): Pair<String, Long> {
        return try {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionName = packageInfo.versionName ?: "unknown"
            val versionCode = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
            Pair(versionName, versionCode)
        } catch (e: PackageManager.NameNotFoundException) {
            Pair("error", 0L)
        }
    }
}