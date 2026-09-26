package com.example.alphabetlauncher.presentation.ui.components

import android.view.View
import androidx.compose.runtime.*
import kotlin.math.abs
import kotlin.math.exp

@Stable
class AlphabetBarState(
    private val maxOffsetPx: Float,
    private val bulgeRadiusPx: Float,
    private val view: View,
    private val onLetterSelected: (Char) -> Unit,
    private val onDragStarted: () -> Unit,
    private val onDragEnded: () -> Unit
) {
    var barHeight by mutableFloatStateOf(0f)
    var touchY by mutableFloatStateOf(0f)
    var isDragging by mutableStateOf(false)
    private var lastHapticChar by mutableStateOf(' ')

    val allItems = listOf("☆") + ('A'..'Z').map { it.toString() } + listOf("•")

    fun getOffset(itemY: Float, bulgeAmplitude: Float, animatedTouchY: Float): Float {
        val distance = abs(animatedTouchY - itemY)
        return if (distance < bulgeRadiusPx) {
            val ratio = distance / bulgeRadiusPx
            val curve = kotlin.math.cos(ratio * (Math.PI / 2)).toFloat()
            val smoothedCurve = Math.pow(curve.toDouble(), 1.2).toFloat()
            -maxOffsetPx * bulgeAmplitude * smoothedCurve
        } else {
            0f
        }
    }

    fun getOffsetY(itemY: Float, bulgeAmplitude: Float, animatedTouchY: Float): Float {
        val distance = itemY - animatedTouchY
        if (abs(distance) < bulgeRadiusPx) {
            val ratio = distance / bulgeRadiusPx // -1 to 1
            // A sine wave ensures 0 offset exactly at the peak, pushing items smoothly outwards
            val spread = kotlin.math.sin(ratio * Math.PI).toFloat()
            return spread * bulgeAmplitude * 20f 
        }
        return 0f
    }

    fun getScale(offset: Float): Float {
        return 1f + (abs(offset) / maxOffsetPx) * 0.8f
    }

    fun getItemY(index: Int): Float {
        return if (barHeight > 0) (index + 0.5f) * (barHeight / allItems.size) else 0f
    }

    fun getSelectedIndex(): Int {
        if (barHeight <= 0) return 0
        val itemHeight = barHeight / allItems.size
        return (touchY / itemHeight).toInt().coerceIn(0, allItems.lastIndex)
    }

    fun processTouch(y: Float) {
        touchY = y
        if (barHeight > 0) {
            val index = getSelectedIndex()
            val selectedText = allItems[index]
            if (selectedText.length == 1) {
                val char = selectedText[0]
                if (char.isLetter() || char == '☆' || char == '•') {
                    if (char != lastHapticChar) {
                        lastHapticChar = char
                        view.performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                    }
                    onLetterSelected(char)
                }
            }
        }
    }

    fun startDrag(y: Float) {
        isDragging = true
        touchY = y
        onDragStarted()
    }

    fun endDrag() {
        isDragging = false
        onDragEnded()
    }
}

@Composable
fun rememberAlphabetBarState(
    maxOffsetPx: Float,
    bulgeRadiusPx: Float,
    view: View,
    onLetterSelected: (Char) -> Unit,
    onDragStarted: () -> Unit,
    onDragEnded: () -> Unit
): AlphabetBarState {
    return remember(maxOffsetPx, bulgeRadiusPx, view, onLetterSelected, onDragStarted, onDragEnded) {
        AlphabetBarState(
            maxOffsetPx = maxOffsetPx,
            bulgeRadiusPx = bulgeRadiusPx,
            view = view,
            onLetterSelected = onLetterSelected,
            onDragStarted = onDragStarted,
            onDragEnded = onDragEnded
        )
    }
}
