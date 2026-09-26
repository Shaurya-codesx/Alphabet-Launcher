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

@Composable
fun AlphabetBar(
    isLeftHanded: Boolean = false,
    modifier: Modifier = Modifier,
    onLetterSelected: (Char) -> Unit = {},
    onDragStarted: () -> Unit = {},
    onDragEnded: () -> Unit = {}
) {
    val view = androidx.compose.ui.platform.LocalView.current
    val density = LocalDensity.current
    
    val maxOffsetPx = with(density) { 90.dp.toPx() }
    val bulgeRadiusPx = with(density) { 160.dp.toPx() }

    val state = rememberAlphabetBarState(
        maxOffsetPx = maxOffsetPx,
        bulgeRadiusPx = bulgeRadiusPx,
        view = view,
        onLetterSelected = onLetterSelected,
        onDragStarted = onDragStarted,
        onDragEnded = onDragEnded
    )


    val bulgeAmplitude by animateFloatAsState(
        targetValue = if (state.isDragging) 1f else 0f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "bulgeAmplitude"
    )


    val animatedTouchY by animateFloatAsState(
        targetValue = state.touchY,
        animationSpec = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium),
        label = "animatedTouchY"
    )

    Box(
        modifier = modifier
            .fillMaxHeight(0.70f)
            .width(40.dp) 
            .onSizeChanged { state.barHeight = it.height.toFloat() }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown()
                    down.consume()
                    state.startDrag(down.position.y)

                    do {
                        val event = awaitPointerEvent()
                        val pointer = event.changes.firstOrNull()
                        if (pointer != null) {
                            pointer.consume()
                            state.processTouch(pointer.position.y)
                        }
                    } while (event.changes.any { it.pressed })

                    state.endDrag()
                }
            }
    ) {
        // The Alphabet Column
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            state.allItems.forEachIndexed { index, item ->
                val itemY = state.getItemY(index)
                val offsetX = state.getOffset(itemY, bulgeAmplitude, animatedTouchY, isLeftHanded)
                val offsetY = state.getOffsetY(itemY, bulgeAmplitude, animatedTouchY)
                val scale = state.getScale(offsetX)

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = if (isLeftHanded) Alignment.CenterStart else Alignment.CenterEnd
                ) {
                    val isSelected = state.isDragging && index == state.getSelectedIndex()
                    Text(
                        text = item,
                        fontSize = 14.sp,
                        fontWeight = if (item.length == 1 && item[0].isLetter()) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                        modifier = Modifier
                            .graphicsLayer {
                                translationX = offsetX
                                translationY = offsetY
                                scaleX = scale
                                scaleY = scale
                            }
                            .padding(start = if (isLeftHanded) 12.dp else 0.dp, end = if (isLeftHanded) 0.dp else 12.dp)
                    )
                }
            }
        }

        // Floating Letter Bubble
        if (state.isDragging && state.barHeight > 0) {
            val index = state.getSelectedIndex()
            val selectedText = state.allItems[index]
            
            if (selectedText.length == 1 && (selectedText[0].isLetter() || selectedText[0] == '☆' || selectedText[0] == '•')) {
                val bubbleSize = 72.dp
                val bubbleSizePx = with(density) { bubbleSize.toPx() }
                
                Box(
                    modifier = Modifier
                        .align(if (isLeftHanded) Alignment.TopStart else Alignment.TopEnd)
                        .graphicsLayer {
                            translationY = animatedTouchY - (bubbleSizePx / 2)
                            translationX = if (isLeftHanded) maxOffsetPx + with(density) { 64.dp.toPx() } else -maxOffsetPx - with(density) { 64.dp.toPx() }
                        }
                        .requiredSize(bubbleSize)
                        .background(
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = selectedText,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
