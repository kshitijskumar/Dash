package org.example.dash.ui.theme

import androidx.compose.ui.graphics.Color

object AppColors {
    // Surfaces
    val Background = Color(0xFF121212)           // Very dark, almost black
    val Surface = Color(0xFF1E1E1E)              // Elevated dark surface
    val SurfaceVariant = Color(0xFF2A2A2A)       // Higher elevation
    
    // Primary
    val Primary = Color(0xFF64B5F6)              // Bright blue (readable on dark)
    val PrimaryVariant = Color(0xFF42A5F5)       // Slightly deeper blue
    val OnPrimary = Color(0xFF000000)            // Black text on primary
    
    // Text
    val TextPrimary = Color(0xFFE3E3E3)          // Off-white, easy on eyes
    val TextSecondary = Color(0xFFB0B0B0)        // Muted gray
    val TextDisabled = Color(0xFF6B6B6B)         // Darker gray
    
    // Borders
    val Border = Color(0xFF3A3A3A)               // Subtle border
    val BorderFocused = Color(0xFF64B5F6)        // Primary color
    
    // Error
    val ErrorContainer = Color(0xFF5D2020)       // Dark red background
    val ErrorText = Color(0xFFEF9A9A)            // Light red text
    
    // Link Card Colors (Dark Mode)
    val LinkColors = listOf(
        0xFFB71C1C, // Dark Red
        0xFF6A1B9A, // Dark Purple
        0xFF1565C0, // Dark Blue
        0xFF00838F, // Dark Cyan
        0xFF2E7D32, // Dark Green
        0xFFF57F17, // Dark Amber
        0xFFD84315, // Dark Orange
        0xFF4E342E, // Dark Brown
        0xFF37474F, // Dark Blue Grey
        0xFFAD1457, // Dark Pink
        0xFF4A148C, // Darker Purple
        0xFF0277BD, // Medium Blue
        0xFF00695C, // Dark Teal
        0xFF558B2F, // Medium Green
        0xFFEF6C00, // Dark Orange
    )
}
