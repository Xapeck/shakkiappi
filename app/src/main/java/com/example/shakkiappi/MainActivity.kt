package com.example.shakkiappi

import android.os.Bundle
import android.os.Vibrator
import android.os.VibrationEffect
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ClockViewModel : ViewModel() {
    private val _whiteTime = MutableStateFlow(300000L)
    val whiteTime: StateFlow<Long> = _whiteTime.asStateFlow()
    
    private val _blackTime = MutableStateFlow(300000L)
    val blackTime: StateFlow<Long> = _blackTime.asStateFlow()
    
    private val _activePlayer = MutableStateFlow("white")
    val activePlayer: StateFlow<String> = _activePlayer.asStateFlow()
    
    private val _whiteMoves = MutableStateFlow(0)
    val whiteMoves: StateFlow<Int> = _whiteMoves.asStateFlow()
    
    private val _blackMoves = MutableStateFlow(0)
    val blackMoves: StateFlow<Int> = _blackMoves.asStateFlow()
    
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    
    private var timerJob: Job? = null
    
    fun startGame(minutes: Int) {
        val timeMs = minutes * 60 * 1000L
        _whiteTime.value = timeMs
        _blackTime.value = timeMs
        _whiteMoves.value = 0
        _blackMoves.value = 0
        _activePlayer.value = "white"
        _isRunning.value = true
        startTimer()
    }
    
    fun pressPlayer(player: String) {
        if (!_isRunning.value) return
        if (_activePlayer.value != player) return
        
        if (player == "white") {
            _whiteMoves.value = _whiteMoves.value + 1
            _activePlayer.value = "black"
        } else {
            _blackMoves.value = _blackMoves.value + 1
            _activePlayer.value = "white"
        }
    }
    
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_isRunning.value) {
                delay(10)
                if (_activePlayer.value == "white") {
                    val newTime = _whiteTime.value - 10
                    if (newTime <= 0) {
                        _whiteTime.value = 0
                        _isRunning.value = false
                        break
                    }
                    _whiteTime.value = newTime
                } else {
                    val newTime = _blackTime.value - 10
                    if (newTime <= 0) {
                        _blackTime.value = 0
                        _isRunning.value = false
                        break
                    }
                    _blackTime.value = newTime
                }
            }
        }
    }
    
    fun reset() {
        timerJob?.cancel()
        _isRunning.value = false
    }
    
    override fun onCleared() {
        super.onCleared()
        timerJob?.cancel()
    }
}

fun formatTime(ms: Long): String {
    val seconds = (ms / 1000).coerceAtLeast(0)
    val minutes = seconds / 60
    val secs = seconds % 60
    val centis = (ms % 1000) / 10
    return String.format("%02d:%02d.%02d", minutes, secs, centis)
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ChessClockApp()
                }
            }
        }
    }
}

@Composable
fun ChessClockApp() {
    val viewModel: ClockViewModel = viewModel()
    val whiteTime by viewModel.whiteTime.collectAsState()
    val blackTime by viewModel.blackTime.collectAsState()
    val activePlayer by viewModel.activePlayer.collectAsState()
    val whiteMoves by viewModel.whiteMoves.collectAsState()
    val blackMoves by viewModel.blackMoves.collectAsState()
    val isRunning by viewModel.isRunning.collectAsState()
    
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Vibrator::class.java) }
    
    var showTimeDialog by remember { mutableStateOf(false) }
    
    fun vibrate() {
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Black player (top)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(if (activePlayer == "black" && isRunning) Color(0xFF4CAF50) else Color(0xFF2C2C2C))
                    .clickable(enabled = isRunning && activePlayer == "black") {
                        vibrate()
                        viewModel.pressPlayer("black")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("MUSTA", color = Color.White, fontSize = 24.sp)
                    Text(formatTime(blackTime), color = Color.White, fontSize = 48.sp)
                    Text("Siirrot: $blackMoves", color = Color.White, fontSize = 18.sp)
                }
            }
            
            Divider(color = Color.White, thickness = 2.dp)
            
            // White player (bottom)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(if (activePlayer == "white" && isRunning) Color(0xFF4CAF50) else Color(0xFFF0F0F0))
                    .clickable(enabled = isRunning && activePlayer == "white") {
                        vibrate()
                        viewModel.pressPlayer("white")
                    },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("VALKOINEN", fontSize = 24.sp)
                    Text(formatTime(whiteTime), fontSize = 48.sp)
                    Text("Siirrot: $whiteMoves", fontSize = 18.sp)
                }
            }
        }
        
        // Settings button
        FloatingActionButton(
            onClick = { showTimeDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Text("⚙️", fontSize = 24.sp)
        }
    }
    
    if (showTimeDialog) {
        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("Valitse aika") },
            text = {
                Column {
                    listOf(1, 3, 5, 10, 15).forEach { minutes ->
                        Button(
                            onClick = {
                                viewModel.startGame(minutes)
                                showTimeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth().padding(4.dp)
                        ) {
                            Text("$minutes minuuttia")
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showTimeDialog = false }) {
                    Text("Peruuta")
                }
            }
        )
    }
}
