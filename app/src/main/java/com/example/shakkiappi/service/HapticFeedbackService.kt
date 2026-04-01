package com.example.REMOVED.service

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HapticFeedbackService @Inject constructor(@ApplicationContext private val ctx: Context) {
    private val vibrator = ctx.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    
    fun vibrate(duration: Long = 50) {
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION") vibrator.vibrate(duration)
        }
    }
}
