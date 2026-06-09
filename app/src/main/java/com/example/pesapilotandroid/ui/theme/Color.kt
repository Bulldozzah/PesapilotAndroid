package com.example.pesapilotandroid.ui.theme

import androidx.compose.ui.graphics.Color

// ─── Navy Trust Palette ──────────────────────────────────────────────────────

// App surfaces
val AppBackground      = Color(0xFFF5F7FB)
val AppCard            = Color(0xFFFFFFFF)
val AppText            = Color(0xFF1A2035)
val AppPrimary         = Color(0xFF1A2B5E)
val AppPrimaryText     = Color(0xFFF7F9FD)
val AppAccentBlue      = Color(0xFF4C7FBD)
val AppSecondaryBg     = Color(0xFFEEF0F7)
val AppMutedBg         = Color(0xFFF1F2F7)
val AppMutedText       = Color(0xFF7A7E90)
val AppBorder          = Color(0xFFDFE1EC)
val AppInputBorder     = Color(0xFFE3E5EF)
val AppGold            = Color(0xFFC9A620)
val AppGreenSuccess    = Color(0xFF28A87A)
val AppAmberWarning    = Color(0xFFE0A020)
val AppRedDestructive  = Color(0xFFCC4433)

// Sidebar
val SidebarBackground  = Color(0xFF101E3A)
val SidebarText        = Color(0xFFE0E4EF)
val SidebarActiveBg    = Color(0xFF1A2B50)
val SidebarActiveText  = Color(0xFFF7F9FD)
val SidebarIconDefault = Color(0xFF9BA8C4)
val SidebarIconActive  = Color(0xFFFFFFFF)
val SidebarBorder      = Color(0xFF1D2E4E)
val SidebarLogoBg      = Color(0xFF4C7FBD)

// Dark mode
val DarkBackground     = Color(0xFF0F1729)
val DarkCard           = Color(0xFF141D35)
val DarkText           = Color(0xFFF2F4FA)
val DarkPrimary        = Color(0xFF6A99D4)
val DarkMutedText      = Color(0xFF8A8FA6)
val DarkBorder         = Color(0x1FFFFFFF)

// Semantic aliases
val Success  = AppGreenSuccess
val Warning  = AppAmberWarning
val Error    = AppRedDestructive
val OnError  = Color(0xFFFFFFFF)

// Chart Colors
val ChartColors = listOf(
    AppGreenSuccess,
    AppAccentBlue,
    AppGold,
    AppRedDestructive,
    Color(0xFF8B5CF6),
    Color(0xFFEC4899),
    Color(0xFF06B6D4),
    Color(0xFF84CC16)
)

// Legacy compat aliases
val Purple80      = AppAccentBlue
val PurpleGrey80  = AppMutedText
val Pink80        = AppGold
val Purple40      = AppPrimary
val PurpleGrey40  = AppMutedText
val Pink40        = AppGold