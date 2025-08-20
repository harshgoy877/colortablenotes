package com.colortablenotes.presentation.theme

import androidx.compose.ui.graphics.Color

// Primary Brand Colors - Soft and professional
val NotesBlue = Color(0xFF2E7D91)
val NotesBlueLight = Color(0xFF4A9BB0)
val NotesBlueDark = Color(0xFF1A5B6B)

// Secondary Colors - Warm and inviting
val NotesOrange = Color(0xFFE17B47)
val NotesOrangeLight = Color(0xFFE89B6B)
val NotesOrangeDark = Color(0xFFB85A2E)

// Background Colors - Clean and easy on eyes
val BackgroundLight = Color(0xFFFAFAFA)
val BackgroundDark = Color(0xFF121212)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceDark = Color(0xFF1E1E1E)

// Note Color Palette - Vibrant but not overwhelming
val NoteColorRed = Color(0xFFE57373)
val NoteColorOrange = Color(0xFFFFB74D)
val NoteColorYellow = Color(0xFFFFF176)
val NoteColorGreen = Color(0xFF81C784)
val NoteColorBlue = Color(0xFF64B5F6)
val NoteColorPurple = Color(0xFFBA68C8)
val NoteColorPink = Color(0xFFF06292)
val NoteColorTeal = Color(0xFF4DD0E1)
val NoteColorBrown = Color(0xFFA1887F)
val NoteColorGrey = Color(0xFF90A4AE)

// Text Colors
val TextPrimary = Color(0xFF2C2C2C)
val TextSecondary = Color(0xFF6B6B6B)
val TextTertiary = Color(0xFF9E9E9E)

// System Colors
val ErrorColor = Color(0xFFE53E3E)
val WarningColor = Color(0xFFED8936)
val SuccessColor = Color(0xFF38A169)
val InfoColor = Color(0xFF3182CE)

// Helper function to get note colors
fun getNoteColors(): List<Pair<String, Color>> = listOf(
    "RED" to NoteColorRed,
    "ORANGE" to NoteColorOrange,
    "YELLOW" to NoteColorYellow,
    "GREEN" to NoteColorGreen,
    "BLUE" to NoteColorBlue,
    "PURPLE" to NoteColorPurple,
    "PINK" to NoteColorPink,
    "TEAL" to NoteColorTeal,
    "BROWN" to NoteColorBrown,
    "GREY" to NoteColorGrey
)

fun getColorByName(colorName: String): Color = when (colorName) {
    "RED" -> NoteColorRed
    "ORANGE" -> NoteColorOrange
    "YELLOW" -> NoteColorYellow
    "GREEN" -> NoteColorGreen
    "BLUE" -> NoteColorBlue
    "PURPLE" -> NoteColorPurple
    "PINK" -> NoteColorPink
    "TEAL" -> NoteColorTeal
    "BROWN" -> NoteColorBrown
    "GREY" -> NoteColorGrey
    else -> Color.Transparent
}
