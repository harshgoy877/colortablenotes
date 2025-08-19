package com.colortablenotes.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.colortablenotes.data.local.entities.Note
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NoteCard(
    note: Note,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    searchQuery: String = ""
) {
    val haptic = LocalHapticFeedback.current
    val noteColor = getNoteColor(note.color)
    val noteIcon = getNoteTypeIcon(note.type)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    onLongClick()
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(48.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(noteColor)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Icon(
                imageVector = noteIcon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = note.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Medium
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = formatDate(note.updatedAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (note.pinned) {
                Icon(
                    imageVector = Icons.Default.PushPin,
                    contentDescription = "Pinned",
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

private fun getNoteColor(colorName: String): Color {
    return when (colorName) {
        "RED" -> Color(0xFFEF5350)
        "ORANGE" -> Color(0xFFFF9800)
        "YELLOW" -> Color(0xFFFFEB3B)
        "GREEN" -> Color(0xFF4CAF50)
        "BLUE" -> Color(0xFF2196F3)
        "PURPLE" -> Color(0xFF9C27B0)
        "PINK" -> Color(0xFFE91E63)
        "BROWN" -> Color(0xFF795548)
        else -> Color.Transparent
    }
}

private fun getNoteTypeIcon(type: String) = when (type) {
    "TEXT" -> Icons.Default.Description
    "CHECKLIST" -> Icons.Default.CheckBox
    "TABLE" -> Icons.Default.GridOn
    else -> Icons.Default.Note
}

private fun formatDate(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    val diffInHours = diffInMillis / (1000 * 60 * 60)
    val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)

    return when {
        diffInHours < 1 -> "Just now"
        diffInHours < 24 -> "${diffInHours}h ago"
        diffInDays < 7 -> "${diffInDays}d ago"
        else -> SimpleDateFormat("MMM dd", Locale.getDefault()).format(date)
    }
}
