package com.example.alphabetlauncher.domain.model

import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.ImageBitmap

data class AppInfo(
    val label: String,
    val packageName: String,
    val icon: Drawable,
    val imageBitmap: ImageBitmap
)
