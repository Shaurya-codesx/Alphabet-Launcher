package com.example.alphabetlauncher.domain.repository

import com.example.alphabetlauncher.domain.model.AppInfo

interface AppRepository {
    suspend fun getInstalledApps(): List<AppInfo>
    fun isFavoritesInitialized(): Boolean
    fun getFavoritePackages(): Set<String>
    fun setFavoritePackages(packages: Set<String>)
    fun toggleFavorite(packageName: String)
}
