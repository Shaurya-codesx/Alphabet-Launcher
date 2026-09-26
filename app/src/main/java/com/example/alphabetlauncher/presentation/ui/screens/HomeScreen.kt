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
import com.example.alphabetlauncher.presentation.ui.components.AlphabetBar
import com.example.alphabetlauncher.presentation.ui.components.AppItem
import com.example.alphabetlauncher.presentation.ui.components.ClockWidget
import android.content.Intent
import androidx.compose.ui.platform.LocalContext

@Composable
fun HomeScreen(
    viewModel: LauncherViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Left side content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(end = 32.dp)
            ) {
                if (!state.isDragging) {
                    // Clock
                    ClockWidget(
                        modifier = Modifier
                            .padding(start = 24.dp, top = 64.dp, bottom = 32.dp)
                    )

                    // Favorites List
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(state.favorites) { app ->
                            AppItem(
                                app = app,
                                onClick = {
                                    val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                    intent?.let { context.startActivity(it) }
                                }
                            )
                        }
                    }
                } else {
                    // Filtered List Header
                    androidx.compose.material3.Text(
                        text = state.selectedLetter?.toString() ?: "",
                        fontSize = 48.sp,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                        modifier = Modifier.padding(start = 24.dp, top = 64.dp, bottom = 16.dp),
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Filtered List
                    if (state.filteredApps.isEmpty() && state.selectedLetter != null) {
                        androidx.compose.material3.Text(
                            text = "No apps",
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 24.dp),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(state.filteredApps) { app ->
                                AppItem(
                                    app = app,
                                    onClick = {
                                        val intent = context.packageManager.getLaunchIntentForPackage(app.packageName)
                                        intent?.let { context.startActivity(it) }
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // Right side: Alphabet Bar
            AlphabetBar(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(vertical = 32.dp),
                onDragStarted = viewModel::onDragStarted,
                onDragEnded = viewModel::onDragEnded,
                onLetterSelected = viewModel::onLetterSelected
            )
        }
    }
}
