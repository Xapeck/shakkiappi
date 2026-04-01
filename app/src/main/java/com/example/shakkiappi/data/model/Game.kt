package com.example.shakkiappi.data.model

data class Game(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val winner: Player,
    val moves: Pair<Int, Int>,
    val duration: Long,
    val timeControl: TimeControl
)

data class GameStats(
    val totalGames: Int = 0,
    val whiteWins: Int = 0,
    val blackWins: Int = 0,
    val averageMoves: Float = 0f,
    val favoriteTimeControl: String = ""
)
