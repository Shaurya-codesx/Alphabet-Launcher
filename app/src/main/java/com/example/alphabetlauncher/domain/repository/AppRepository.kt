package com.example.alphabetlauncher.domain.repository

import com.example.alphabetlauncher.domain.model.AppInfo

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
}
