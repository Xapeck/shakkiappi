package com.example.REMOVED.service

import android.content.Context
import android.media.MediaPlayer
import com.example.REMOVED.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioFeedbackService @Inject constructor(@ApplicationContext private val ctx: Context) {
    
    suspend fun playClickSound() = playSound(R.raw.click_sound)
    suspend fun playGameEndSound() = playSound(R.raw.game_end)
    
    private fun playSound(res: Int) {
        try {
            MediaPlayer.create(ctx, res).apply { setOnCompletionListener { release() }; start() }
        } catch (e: Exception) { }
    }
}
