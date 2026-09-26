package com.example.alphabetlauncher.presentation

import com.example.alphabetlauncher.domain.model.AppInfo

data class LauncherState(
    val allApps: List<AppInfo> = emptyList(),
    val isDragging: Boolean = false,
    val selectedLetter: Char? = null,
    val filteredApps: List<AppInfo> = emptyList(),
    val favorites: List<AppInfo> = emptyList(),
    val isSearchVisible: Boolean = false,
    val searchQuery: String = ""
)
