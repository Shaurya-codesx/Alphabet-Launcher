package com.example.alphabetlauncher.presentation.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import com.example.alphabetlauncher.domain.model.AppInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppItem(
    app: AppInfo,
    onClick: () -> Unit,
    onLongClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    isLeftHanded: Boolean = false
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick
            )
            .padding(vertical = 16.dp, horizontal = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isLeftHanded) Arrangement.End else Arrangement.Start
    ) {
        if (isLeftHanded) {
            Text(
                text = app.label,
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Image(
                bitmap = app.imageBitmap,
                contentDescription = app.label,
                modifier = Modifier.size(42.dp)
            )
        } else {
            Image(
                bitmap = app.imageBitmap,
                contentDescription = app.label,
                modifier = Modifier.size(42.dp)
            )
            Spacer(modifier = Modifier.width(20.dp))
            Text(
                text = app.label,
                fontSize = 16.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                letterSpacing = 0.5.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f)
            )
        }
    }
}
