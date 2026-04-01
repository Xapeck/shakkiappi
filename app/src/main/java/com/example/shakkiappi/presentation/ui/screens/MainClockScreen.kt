package com.example.REMOVED.presentation.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.REMOVED.data.model.GameState
import com.example.REMOVED.data.model.Player
import com.example.REMOVED.presentation.ui.components.AnimatedClockDisplay
import com.example.REMOVED.presentation.ui.components.ClockStyle
import com.example.REMOVED.presentation.ui.components.TimeControlDialog
import com.example.REMOVED.presentation.viewmodel.ClockViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainClockScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToStats: () -> Unit,
    viewModel: ClockViewModel = hiltViewModel()
) {
    val playerTimes by viewModel.playerTimes.collectAsState()
    val activePlayer by viewModel.activePlayer.collectAsState()
    val moveCounts by viewModel.moveCounts.collectAsState()
    val gameState by viewModel.gameState.collectAsState()
    val clockStyle by viewModel.clockStyle.collectAsState()
    
    var showTimeDialog by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlayerClockSection(
                player = Player.BLACK,
                time = playerTimes.second,
                moves = moveCounts.second,
                isActive = activePlayer == Player.BLACK,
                isRunning = gameState == GameState.RUNNING,
                clockStyle = clockStyle,
                onPlayerPress = { viewModel.playerPressed(Player.BLACK) },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
            
            Divider(
                color = MaterialTheme.colorScheme.primary,
                thickness = 2.dp,
                modifier = Modifier.fillMaxWidth()
            )
            
            PlayerClockSection(
                player = Player.WHITE,
                time = playerTimes.first,
                moves = moveCounts.first,
                isActive = activePlayer == Player.WHITE,
                isRunning = gameState == GameState.RUNNING,
                clockStyle = clockStyle,
                onPlayerPress = { viewModel.playerPressed(Player.WHITE) },
                modifier = Modifier.weight(1f).fillMaxWidth()
            )
        }
        
        FloatingActionButton(
            onClick = { showTimeDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Asetukset")
        }
        
        FloatingActionButton(
            onClick = onNavigateToStats,
            modifier = Modifier.align(Alignment.BottomStart).padding(16.dp),
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Icon(Icons.Default.Star, contentDescription = "Tilastot")
        }
    }
    
    if (showTimeDialog) {
        TimeControlDialog(
            onDismiss = { showTimeDialog = false },
            onTimeControlSelected = { viewModel.startGame(it) }
        )
    }
}

@Composable
fun PlayerClockSection(
    player: Player,
    time: Long,
    moves: Int,
    isActive: Boolean,
    isRunning: Boolean,
    clockStyle: ClockStyle,
    onPlayerPress: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isActive && isRunning) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    
    Box(
        modifier = modifier
            .background(backgroundColor)
            .clickable(enabled = isActive && isRunning) { onPlayerPress() },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(player.name, style = MaterialTheme.typography.headlineSmall)
            Spacer(modifier = Modifier.height(16.dp))
            AnimatedClockDisplay(time, isActive, clockStyle)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Siirrot: $moves", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
