package com.example.alphabetlauncher.presentation.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun AlphabetBar(
    modifier: Modifier = Modifier
) {
    val alphabet = ('A'..'Z').toList()

    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(32.dp),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "☆",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
        
        alphabet.forEach { letter ->
            Text(
                text = letter.toString(),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Text(
            text = "•",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}
