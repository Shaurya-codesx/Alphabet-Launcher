package com.example.alphabetlauncher.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.example.alphabetlauncher.domain.model.AppInfo
import com.example.alphabetlauncher.domain.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepositoryImpl(private val context: Context) : AppRepository {

    // Cache the list in memory
    private var cachedApps: List<AppInfo>? = null
    private val prefs = context.getSharedPreferences("launcher_prefs", Context.MODE_PRIVATE)

    override fun isFavoritesInitialized(): Boolean {
        return prefs.getBoolean("fav_init", false)
    }

    override fun getFavoritePackages(): Set<String> {
        return prefs.getStringSet("favorites", emptySet()) ?: emptySet()
    }

    override fun setFavoritePackages(packages: Set<String>) {
        prefs.edit().putStringSet("favorites", packages).putBoolean("fav_init", true).apply()
    }

    override fun toggleFavorite(packageName: String) {
        val current = getFavoritePackages().toMutableSet()
        if (current.contains(packageName)) {
            current.remove(packageName)
        } else {
            current.add(packageName)
        }
        setFavoritePackages(current)
    }

    override suspend fun getInstalledApps(): List<AppInfo> = withContext(Dispatchers.IO) {
        cachedApps?.let { return@withContext it }

        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        // Use queryIntentActivities to get launchable apps
        val resolveInfos: List<ResolveInfo> = pm.queryIntentActivities(intent, 0)
        
        val apps = resolveInfos.mapNotNull { resolveInfo ->
            val packageName = resolveInfo.activityInfo.packageName
            // Exclude the launcher from the list to avoid recursive loop
            if (packageName == context.packageName) return@mapNotNull null

            val label = resolveInfo.loadLabel(pm).toString()
            val icon = resolveInfo.loadIcon(pm)

            AppInfo(
                label = label,
                packageName = packageName,
                icon = icon
            )
        }.sortedBy { it.label.lowercase() }

        cachedApps = apps
        return@withContext apps
    }
}
