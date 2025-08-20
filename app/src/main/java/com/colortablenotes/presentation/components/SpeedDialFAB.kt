package com.colortablenotes.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
        animationSpec = tween(300, easing = FastOutSlowInEasing),
        label = "rotation"
    )

    val fabScale by animateFloatAsState(
        targetValue = if (expanded) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "fabScale"
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.Bottom
    ) {
        if (expanded) {
            // Table option
            SpeedDialOption(
                icon = Icons.Default.GridOn,
                label = "Table Note",
                description = "Create structured data",
                onClick = {
                    onCreateTableNote()
                    expanded = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Checklist option
            SpeedDialOption(
                icon = Icons.Default.CheckBox,
                label = "Checklist",
                description = "Track tasks & todos",
                onClick = {
                    onCreateChecklistNote()
                    expanded = false
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text option
            SpeedDialOption(
                icon = Icons.Default.Description,
                label = "Text Note",
                description = "Write your thoughts",
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
            modifier = Modifier.scale(fabScale),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary
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
    description: String,
    onClick: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.scale(scale)
    ) {
        Card(
            modifier = Modifier.padding(end = 12.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        FloatingActionButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp),
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
