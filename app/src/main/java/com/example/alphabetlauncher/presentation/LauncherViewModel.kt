package com.example.alphabetlauncher.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alphabetlauncher.domain.model.AppInfo
import com.example.alphabetlauncher.domain.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LauncherViewModel @Inject constructor(
    private val appRepository: AppRepository
) : ViewModel() {

    private val _state = MutableStateFlow(LauncherState())
    val state: StateFlow<LauncherState> = _state.asStateFlow()

    init {
        loadApps()
    }

    private fun loadApps() {
        viewModelScope.launch {
            val apps = appRepository.getInstalledApps()
            val favorites = apps.take(5)
            
            _state.update { 
                it.copy(
                    allApps = apps,
                    favorites = favorites
                ) 
            }
        }
    }

    fun onDragStarted() {
        _state.update { it.copy(isDragging = true) }
    }

    fun onDragEnded() {
        _state.update { 
            it.copy(
                isDragging = false
            ) 
        }
    }

    fun clearSelection() {
        _state.update {
            it.copy(
                selectedLetter = null,
                filteredApps = emptyList()
            )
        }
    }

    fun onSearchSwipeUp() {
        _state.update { it.copy(isSearchVisible = true, selectedLetter = null, filteredApps = emptyList()) }
    }

    fun closeSearch() {
        _state.update { it.copy(isSearchVisible = false, searchQuery = "", filteredApps = emptyList()) }
    }

    fun onSearchQueryChanged(query: String) {
        val filtered = if (query.isBlank()) {
            emptyList()
        } else {
            _state.value.allApps.filter { app ->
                app.label.contains(query, ignoreCase = true)
            }
        }
        _state.update { 
            it.copy(
                searchQuery = query,
                filteredApps = filtered
            ) 
        }
    }

    fun onLetterSelected(letter: Char) {
        if (letter == '☆' || letter == '•') {
            clearSelection()
            return
        }
        if (_state.value.selectedLetter == letter) return
        
        val filtered = _state.value.allApps.filter { app ->
            app.label.startsWith(letter, ignoreCase = true)
        }
        
        _state.update { 
            it.copy(
                selectedLetter = letter,
                filteredApps = filtered
            ) 
        }
    }
}
