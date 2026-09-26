package com.example.alphabetlauncher.presentation.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlin.math.exp

@Composable
fun AlphabetBar(
    modifier: Modifier = Modifier,
    onLetterSelected: (Char) -> Unit = {},
    onDragStarted: () -> Unit = {},
    onDragEnded: () -> Unit = {}
) {
    val alphabet = ('A'..'Z').toList()
    val allItems = listOf("☆") + alphabet.map { it.toString() } + listOf("•")
    
    var barHeight by remember { mutableFloatStateOf(0f) }
    var touchY by remember { mutableFloatStateOf(-1f) }
    var isDragging by remember { mutableStateOf(false) }


    val animatedTouchY by animateFloatAsState(
        targetValue = if (isDragging) touchY else -1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "touchY"
    )

    // Density to convert dp to px
    val density = LocalDensity.current
    val maxOffsetPx = with(density) { 80.dp.toPx() } // How far left the letters bulge
    val bulgeRadiusPx = with(density) { 120.dp.toPx() } // How wide the bulge is vertically

    Box(
        modifier = modifier
            .fillMaxHeight(0.70f)
            .width(40.dp) 
            .onSizeChanged { barHeight = it.height.toFloat() }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    isDragging = true
                    touchY = down.position.y
                    onDragStarted()

                    do {
                        val event = awaitPointerEvent()
                        val pointer = event.changes.firstOrNull()
                        if (pointer != null) {
                            touchY = pointer.position.y
                            
                            // Determine which letter is selected
                            if (barHeight > 0) {
                                val itemHeight = barHeight / allItems.size
                                val index = (touchY / itemHeight).toInt().coerceIn(0, allItems.lastIndex)
                                val selectedText = allItems[index]
                                if (selectedText.length == 1 && selectedText[0].isLetter()) {
                                    onLetterSelected(selectedText[0])
                                }
                            }
                        }
                    } while (event.changes.any { it.pressed })

                    isDragging = false
                    onDragEnded()
                }
            }
    ) {
        // The Alphabet Column
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            allItems.forEachIndexed { index, item ->
                // Calculate distance from touch point
                val itemY = if (barHeight > 0) (index + 0.5f) * (barHeight / allItems.size) else 0f
                
                // Calculate Gaussian curve offset
                val distance = if (isDragging) abs(touchY - itemY) else if (animatedTouchY >= 0) abs(animatedTouchY - itemY) else 9999f
                val offset = if (distance < bulgeRadiusPx) {
                    -maxOffsetPx * exp(-(distance * distance) / (2 * (bulgeRadiusPx / 2) * (bulgeRadiusPx / 2)))
                } else {
                    0f
                }
                
                // Size increases slightly at the peak of the curve
                val scale = 1f + (abs(offset) / maxOffsetPx) * 0.5f

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = item,
                        fontSize = 11.sp,
                        fontWeight = if (item.length == 1 && item[0].isLetter()) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .graphicsLayer {
                                translationX = offset
                                scaleX = scale
                                scaleY = scale
                            }
                            .padding(end = 12.dp)
                    )
                }
            }
        }

        // Floating Letter Bubble (tracks the finger)
        if (isDragging && barHeight > 0) {
            val itemHeight = barHeight / allItems.size
            val index = (touchY / itemHeight).toInt().coerceIn(0, allItems.lastIndex)
            val selectedText = allItems[index]
            
            if (selectedText.length == 1 && selectedText[0].isLetter()) {
                val bubbleSize = 56.dp
                val bubbleSizePx = with(density) { bubbleSize.toPx() }
                
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .graphicsLayer {
                            // Center the bubble on the Y axis of the finger
                            translationY = touchY - (bubbleSizePx / 2)
                            // Push it left of the bulge (maxOffset + extra padding)
                            translationX = -maxOffsetPx - with(density) { 32.dp.toPx() }
                        }
                        .size(bubbleSize)
                        .background(
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.15f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedText,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
