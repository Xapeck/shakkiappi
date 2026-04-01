package com.example.REMOVED.utils

object TimeFormatter {
    fun formatTime(ms: Long): String {
        val sec = (ms / 1000).coerceAtLeast(0)
        return String.format("%02d:%02d.%02d", sec / 60, sec % 60, (ms % 1000) / 10)
    }
}
