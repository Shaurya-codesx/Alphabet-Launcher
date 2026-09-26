package com.example.alphabetlauncher.presentation.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.alphabetlauncher.presentation.LauncherViewModel
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.ui.input.pointer.pointerInput
import com.example.alphabetlauncher.presentation.ui.components.AlphabetBar
import com.example.alphabetlauncher.presentation.ui.components.AppItem
import com.example.alphabetlauncher.presentation.ui.components.ClockWidget
import android.content.Intent
import androidx.compose.ui.platform.LocalContext
import android.os.Build
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Button
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.setValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var showDefaultPrompt by remember { mutableStateOf(false) }

    androidx.compose.runtime.DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                val isDefault = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val roleManager = context.getSystemService(android.content.Context.ROLE_SERVICE) as android.app.role.RoleManager
                    roleManager.isRoleHeld(android.app.role.RoleManager.ROLE_HOME)
                } else {
                    val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_HOME) }
                    val resolveInfo = context.packageManager.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY)
                    resolveInfo?.activityInfo?.packageName == context.packageName
                }
                if (!isDefault) {
                    showDefaultPrompt = true
                } else {
                    showDefaultPrompt = false
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    androidx.activity.compose.BackHandler(enabled = state.selectedLetter != null) {
        viewModel.clearSelection()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures { _, dragAmount ->
                        if (dragAmount < -20) {
                            viewModel.onSearchSwipeUp()
                        }
                    }
                }
        ) {
            val isLeftHanded = state.isLeftHandedMode

            // Main content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        start = if (isLeftHanded) 32.dp else 0.dp,
                        end = if (isLeftHanded) 0.dp else 32.dp
                    ),
                horizontalAlignment = if (isLeftHanded) Alignment.End else Alignment.Start
            ) {
                if (state.selectedLetter == null) {
                    // Clock
                    ClockWidget(
                        modifier = Modifier
                            .padding(
                                start = if (isLeftHanded) 0.dp else 24.dp, 
                                end = if (isLeftHanded) 24.dp else 0.dp, 
                                top = 64.dp, 
                                bottom = 32.dp
                            )
                    )

                    // Favorites List
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.favorites) { app ->
                            AppItem(
                                app = app,
                                isLeftHanded = isLeftHanded,
                                onClick = { viewModel.onAppClicked(app.packageName, context) },
                                onLongClick = {
                                    viewModel.toggleFavorite(app.packageName)
                                }
                            )
                        }
                    }
                } else {
                    // Filtered List Header
                    androidx.compose.material3.Text(
                        text = state.selectedLetter?.toString() ?: "",
                        fontSize = 72.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Light,
                        modifier = Modifier.padding(
                            start = if (isLeftHanded) 0.dp else 24.dp, 
                            end = if (isLeftHanded) 24.dp else 0.dp, 
                            top = 64.dp, 
                            bottom = 24.dp
                        ),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Filtered List
                    if (state.filteredApps.isEmpty() && state.selectedLetter != null) {
                        androidx.compose.material3.Text(
                            text = "No apps",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(
                                start = if (isLeftHanded) 0.dp else 24.dp, 
                                end = if (isLeftHanded) 24.dp else 0.dp
                            ),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.filteredApps) { app ->
                                AppItem(
                                    app = app,
                                    isLeftHanded = isLeftHanded,
                                    onClick = { viewModel.onAppClicked(app.packageName, context) }
                                )
                            }
                        }
                    }
                }
            }

            AlphabetBar(
                isLeftHanded = isLeftHanded,
                modifier = Modifier
                    .align(if (isLeftHanded) Alignment.CenterStart else Alignment.CenterEnd)
                    .padding(vertical = 32.dp),
                onDragStarted = viewModel::onDragStarted,
                onDragEnded = viewModel::onDragEnded,
                onLetterSelected = viewModel::onLetterSelected
            )

            AnimatedVisibility(
                visible = state.isSearchVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                SearchScreen(
                    state = state,
                    onQueryChanged = viewModel::onSearchQueryChanged,
                    onToggleFavorite = viewModel::toggleFavorite,
                    onClose = viewModel::closeSearch
                )
            }

            AnimatedVisibility(
                visible = state.isSettingsVisible,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                SettingsScreen(
                    isLeftHandedMode = state.isLeftHandedMode,
                    onToggleLeftHandedMode = viewModel::toggleLeftHandedMode,
                    onClose = viewModel::closeSettings
                )
            }

            if (showDefaultPrompt) {
                AlertDialog(
                    onDismissRequest = { showDefaultPrompt = false },
                    title = { androidx.compose.material3.Text("Set as Default Launcher") },
                    text = { androidx.compose.material3.Text("Alphabet Launcher is not currently set as your default home app. Would you like to set it now?") },
                    confirmButton = {
                        TextButton(onClick = {
                            val intent = Intent(Settings.ACTION_HOME_SETTINGS)
                            context.startActivity(intent)
                            showDefaultPrompt = false
                        }) {
                            androidx.compose.material3.Text("Settings")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDefaultPrompt = false }) {
                            androidx.compose.material3.Text("Later")
                        }
                    }
                )
            }
        }
    }
}
