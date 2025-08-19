package com.colortablenotes.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp

@Composable
fun SpeedDialFAB(
    onCreateTextNote: () -> Unit,
    onCreateChecklistNote: () -> Unit,
    onCreateTableNote: () -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current

    val rotationAngle by animateFloatAsState(
        targetValue = if (expanded) 45f else 0f,
        animationSpec = tween(300),
        label = "rotation"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (expanded) {
            SpeedDialOption(
                icon = Icons.Default.GridOn,
                label = "Table",
                onClick = {
                    onCreateTableNote()
                    expanded = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpeedDialOption(
                icon = Icons.Default.CheckBox,
                label = "Checklist",
                onClick = {
                    onCreateChecklistNote()
                    expanded = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            SpeedDialOption(
                icon = Icons.Default.Description,
                label = "Text",
                onClick = {
                    onCreateTextNote()
                    expanded = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        FloatingActionButton(
            onClick = {
                expanded = !expanded
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            },
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = if (expanded) "Close" else "Add note",
                modifier = Modifier.rotate(rotationAngle)
            )
        }
    }
}

@Composable
private fun SpeedDialOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.scale(scale)
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 2.dp,
            shape = CircleShape,
            modifier = Modifier.padding(end = 8.dp)
        ) {
            Text(
                text = label,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium
            )
        }

        SmallFloatingActionButton(
            onClick = onClick,
            containerColor = MaterialTheme.colorScheme.secondary
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label
            )
        }
    }
}
