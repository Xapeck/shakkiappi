package com.example.REMOVED

import android.os.Bundle
import android.os.Vibrator
import android.os.VibrationEffect
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

data class TimePreset(val name: String, val minutes: Int, val incrementSeconds: Int)

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
    
    private val _incrementSeconds = MutableStateFlow(0)
    val incrementSeconds: StateFlow<Int> = _incrementSeconds.asStateFlow()
    
    private var timerJob: Job? = null
    private var selectedMinutes = 3
    private var selectedIncrement = 0
    
    fun selectTime(minutes: Int, incrementSeconds: Int) {
        selectedMinutes = minutes
        selectedIncrement = incrementSeconds
        val timeMs = minutes * 60 * 1000L
        _whiteTime.value = timeMs
        _blackTime.value = timeMs
        _whiteMoves.value = 0
        _blackMoves.value = 0
        _activePlayer.value = "white"
        _isRunning.value = false
        _incrementSeconds.value = incrementSeconds
        stopTimer()
    }
    
    fun startGame() {
        _isRunning.value = true
        startTimer()
    }
    
    fun pressPlayer(player: String) {
        if (!_isRunning.value) return
        if (_activePlayer.value != player) return
        
        if (player == "white") {
            _whiteMoves.value = _whiteMoves.value + 1
            // Lisää aikaa (increment)
            if (_incrementSeconds.value > 0) {
                _whiteTime.value = _whiteTime.value + (_incrementSeconds.value * 1000L)
            }
            _activePlayer.value = "black"
        } else {
            _blackMoves.value = _blackMoves.value + 1
            // Lisää aikaa (increment)
            if (_incrementSeconds.value > 0) {
                _blackTime.value = _blackTime.value + (_incrementSeconds.value * 1000L)
            }
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
    
    private fun stopTimer() {
        timerJob?.cancel()
        timerJob = null
    }
    
    fun reset() {
        stopTimer()
        _isRunning.value = false
    }
    
    override fun onCleared() {
        super.onCleared()
        stopTimer()
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
    val incrementSeconds by viewModel.incrementSeconds.collectAsState()
    
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
    
    // Ajan esiasetukset
    val timePresets = listOf(
        TimePreset("Bullet", 1, 0),
        TimePreset("Blitz", 3, 0),
        TimePreset("Blitz +2", 3, 2),
        TimePreset("Rapid", 5, 0),
        TimePreset("Rapid +3", 5, 3),
        TimePreset("Classical", 10, 0),
        TimePreset("Classical +5", 10, 5),
        TimePreset("Custom", 0, 0)
    )
    
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
                    if (incrementSeconds > 0 && !isRunning) {
                        Text("+${incrementSeconds}s/siirto", color = Color.White, fontSize = 14.sp)
                    }
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
                    if (incrementSeconds > 0 && !isRunning) {
                        Text("+${incrementSeconds}s/siirto", fontSize = 14.sp)
                    }
                }
            }
        }
        
        // Settings button (only when game not running)
        if (!isRunning) {
            FloatingActionButton(
                onClick = { showTimeDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Text("⚙️", fontSize = 24.sp)
            }
        }
        
        // Start button (when time selected but game not running)
        if (!isRunning && whiteTime > 0 && whiteTime < Long.MAX_VALUE) {
            Button(
                onClick = { viewModel.startGame() },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("ALOITA PELI", fontSize = 20.sp, color = Color.White)
            }
        }
    }
    
    if (showTimeDialog) {
        AlertDialog(
            onDismissRequest = { showTimeDialog = false },
            title = { Text("Valitse aikakontrolli", fontSize = 20.sp) },
            text = {
                LazyColumn(
                    modifier = Modifier.heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(timePresets) { preset ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (preset.name == "Custom") {
                                        // Custom dialog would go here
                                        viewModel.selectTime(5, 0)
                                    } else {
                                        viewModel.selectTime(preset.minutes, preset.incrementSeconds)
                                    }
                                    showTimeDialog = false
                                },
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(preset.name, fontSize = 18.sp)
                                if (preset.incrementSeconds > 0) {
                                    Text("${preset.minutes} min + ${preset.incrementSeconds}s", fontSize = 16.sp)
                                } else {
                                    Text("${preset.minutes} min", fontSize = 16.sp)
                                }
                            }
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
