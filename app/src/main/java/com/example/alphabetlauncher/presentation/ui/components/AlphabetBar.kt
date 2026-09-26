package com.example.alphabetlauncher.presentation.ui.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
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
    
    var barHeight by remember { mutableStateOf(0f) }
    var touchY by remember { mutableStateOf(-1f) }
    var isDragging by remember { mutableStateOf(false) }


    val animatedTouchY by animateFloatAsState(
        targetValue = if (isDragging) touchY else -1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "touchY"
    )

    val maxOffset = 250f // Max pixels to pull left
    val bulgeRadius = 300f // How wide the bulge is vertically

    Column(
        modifier = modifier
            .fillMaxHeight(0.65f)
            .width(48.dp)
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
            },
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        allItems.forEachIndexed { index, item ->

            val itemY = if (barHeight > 0) (index + 0.5f) * (barHeight / allItems.size) else 0f
            

            val distance = if (isDragging) abs(touchY - itemY) else if (animatedTouchY >= 0) abs(animatedTouchY - itemY) else 9999f
            val offset = if (distance < bulgeRadius) {
                -maxOffset * exp(-(distance * distance) / (2 * (bulgeRadius / 2) * (bulgeRadius / 2)))
            } else {
                0f
            }
            

            val scale = 1f + (abs(offset) / maxOffset) * 0.5f

            Text(
                text = item,
                fontSize = 10.sp,
                fontWeight = if (item.length == 1 && item[0].isLetter()) FontWeight.Bold else FontWeight.Normal,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier
                    .graphicsLayer {
                        translationX = offset
                        scaleX = scale
                        scaleY = scale
                    }
                    .padding(vertical = 1.dp)
            )
        }
    }

    // Letter Bubble
    if (isDragging) {
        val itemHeight = if (barHeight > 0) barHeight / allItems.size else 0f
        val index = if (itemHeight > 0) (touchY / itemHeight).toInt().coerceIn(0, allItems.lastIndex) else -1
        if (index >= 0) {
            val selectedText = allItems[index]
            if (selectedText.length == 1 && selectedText[0].isLetter()) {
                Box(
                    modifier = Modifier
                        .offset(x = (-120).dp, y = with(androidx.compose.ui.platform.LocalDensity.current) { (touchY - barHeight / 2).toDp() })
                        .size(80.dp)
                        .graphicsLayer {
                            shape = androidx.compose.foundation.shape.CircleShape
                            clip = true
                            shadowElevation = 8f
                        }
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedText,
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}
