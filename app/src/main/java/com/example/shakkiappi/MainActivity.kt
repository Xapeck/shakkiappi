package com.example.shakkiappi

import android.os.Bundle
import android.os.Vibrator
import android.os.VibrationEffect
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
    private val _whiteTime = MutableStateFlow(180000L)
    val whiteTime: StateFlow<Long> = _whiteTime.asStateFlow()
    
    private val _blackTime = MutableStateFlow(180000L)
    val blackTime: StateFlow<Long> = _blackTime.asStateFlow()
    
    private val _activePlayer = MutableStateFlow<String?>(null)
    val activePlayer: StateFlow<String?> = _activePlayer.asStateFlow()
    
    private val _whiteMoves = MutableStateFlow(0)
    val whiteMoves: StateFlow<Int> = _whiteMoves.asStateFlow()
    
    private val _blackMoves = MutableStateFlow(0)
    val blackMoves: StateFlow<Int> = _blackMoves.asStateFlow()
    
    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning.asStateFlow()
    
    private val _isPaused = MutableStateFlow(false)
    val isPaused: StateFlow<Boolean> = _isPaused.asStateFlow()
    
    private val _incrementSeconds = MutableStateFlow(0)
    val incrementSeconds: StateFlow<Int> = _incrementSeconds.asStateFlow()
    
    private val _selectedMinutes = MutableStateFlow(3)
    val selectedMinutes: StateFlow<Int> = _selectedMinutes.asStateFlow()
    
    private val _selectedIncrement = MutableStateFlow(0)
    val selectedIncrement: StateFlow<Int> = _selectedIncrement.asStateFlow()
    
    private var timerJob: Job? = null
    
    init {
        _whiteTime.value = 180000L
        _blackTime.value = 180000L
        _selectedMinutes.value = 3
        _selectedIncrement.value = 0
        _incrementSeconds.value = 0
    }
    
    fun selectTime(minutes: Int, incrementSeconds: Int) {
        _selectedMinutes.value = minutes
        _selectedIncrement.value = incrementSeconds
        val timeMs = minutes * 60 * 1000L
        _whiteTime.value = timeMs
        _blackTime.value = timeMs
        _whiteMoves.value = 0
        _blackMoves.value = 0
        _activePlayer.value = null
        _isRunning.value = false
        _isPaused.value = false
        _incrementSeconds.value = incrementSeconds
        stopTimer()
    }
    
    fun startGame() {
        _isRunning.value = true
        _isPaused.value = false
        _activePlayer.value = "white"
        startTimer()
    }
    
    fun pauseGame() {
        if (_isRunning.value && !_isPaused.value) {
            _isPaused.value = true
            stopTimer()
        }
    }
    
    fun resumeGame() {
        if (_isRunning.value && _isPaused.value) {
            _isPaused.value = false
            startTimer()
        }
    }
    
    fun resetToTimeSelection() {
        stopTimer()
        _isRunning.value = false
        _isPaused.value = false
        _activePlayer.value = null
        val minutes = if (_selectedMinutes.value > 0) _selectedMinutes.value else 3
        val timeMs = minutes * 60 * 1000L
        _whiteTime.value = timeMs
        _blackTime.value = timeMs
        _whiteMoves.value = 0
        _blackMoves.value = 0
    }
    
    fun pressPlayer(player: String) {
        if (!_isRunning.value && player == "white") {
            startGame()
            return
        }
        
        if (!_isRunning.value || _isPaused.value) return
        if (_activePlayer.value != player) return
        
        if (player == "white") {
            _whiteMoves.value = _whiteMoves.value + 1
            if (_incrementSeconds.value > 0) {
                _whiteTime.value = _whiteTime.value + (_incrementSeconds.value * 1000L)
            }
            _activePlayer.value = "black"
        } else {
            _blackMoves.value = _blackMoves.value + 1
            if (_incrementSeconds.value > 0) {
                _blackTime.value = _blackTime.value + (_incrementSeconds.value * 1000L)
            }
            _activePlayer.value = "white"
        }
    }
    
    private fun startTimer() {
        timerJob = viewModelScope.launch {
            while (_isRunning.value && !_isPaused.value) {
                delay(10)
                if (_activePlayer.value == "white") {
                    val newTime = _whiteTime.value - 10
                    if (newTime <= 0) {
                        _whiteTime.value = 0
                        _isRunning.value = false
                        _isPaused.value = false
                        _activePlayer.value = null
                        break
                    }
                    _whiteTime.value = newTime
                } else if (_activePlayer.value == "black") {
                    val newTime = _blackTime.value - 10
                    if (newTime <= 0) {
                        _blackTime.value = 0
                        _isRunning.value = false
                        _isPaused.value = false
                        _activePlayer.value = null
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
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
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
    val isPaused by viewModel.isPaused.collectAsState()
    val incrementSeconds by viewModel.incrementSeconds.collectAsState()
    val selectedMinutes by viewModel.selectedMinutes.collectAsState()
    val selectedIncrement by viewModel.selectedIncrement.collectAsState()
    
    val context = LocalContext.current
    val vibrator = remember { context.getSystemService(Vibrator::class.java) }
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    
    var showSettingsDialog by remember { mutableStateOf(false) }
    var showCustomDialog by remember { mutableStateOf(false) }
    var customMinutes by remember { mutableStateOf("5") }
    var customIncrement by remember { mutableStateOf("0") }
    
    fun vibrate() {
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(50)
        }
    }
    
    val timePresets = listOf(
        TimePreset("Bullet", 1, 0),
        TimePreset("Blitz", 3, 0),
        TimePreset("Blitz +2", 3, 2),
        TimePreset("Rapid", 5, 0),
        TimePreset("Rapid +3", 5, 3),
        TimePreset("Classical", 10, 0),
        TimePreset("Classical +5", 10, 5),
        TimePreset("Classical +10", 15, 10),
        TimePreset("Custom", 0, 0)
    )
    
    // Yhteiset värit ja tyylit
    val whiteBg = if (isPaused) Color(0xFFCCCCCC) 
                  else if (activePlayer == "white" && isRunning) Color(0xFF4CAF50)
                  else if (!isRunning && activePlayer == null) Color(0xFFFFC107)
                  else Color(0xFFF0F0F0)
    
    val blackBg = if (isPaused) Color(0xFF666666)
                  else if (activePlayer == "black" && isRunning) Color(0xFF4CAF50)
                  else Color(0xFF2C2C2C)
    
    val whiteTextColor = if (whiteBg == Color(0xFFF0F0F0) || whiteBg == Color(0xFFFFC107) || whiteBg == Color(0xFFCCCCCC)) Color.Black else Color.White
    val blackTextColor = Color.White
    
    Box(modifier = Modifier.fillMaxSize()) {
        
        if (isLandscape) {
            // Vaakamoodi: pelaajat vierekkäin
            Row(modifier = Modifier.fillMaxSize()) {
                // Musta pelaaja (vasen)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(blackBg)
                        .clickable(enabled = isRunning && !isPaused && activePlayer == "black") {
                            vibrate()
                            viewModel.pressPlayer("black")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        Text("MUSTA", color = blackTextColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(formatTime(blackTime), color = blackTextColor, fontSize = 52.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("Siirrot: $blackMoves", color = blackTextColor, fontSize = 14.sp)
                        if (incrementSeconds > 0 && !isRunning) Text("+${incrementSeconds}s/siirto", color = blackTextColor.copy(alpha = 0.8f), fontSize = 12.sp)
                        if (isPaused) Text("⏸ TAUKO", color = Color.Yellow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Divider(color = Color.White, thickness = 2.dp, modifier = Modifier.fillMaxHeight().width(2.dp))
                
                // Valkoinen pelaaja (oikea)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(whiteBg)
                        .clickable { 
                            vibrate()
                            viewModel.pressPlayer("white")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        Text("VALKOINEN", color = whiteTextColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Text(formatTime(whiteTime), color = whiteTextColor, fontSize = 52.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("Siirrot: $whiteMoves", color = whiteTextColor, fontSize = 14.sp)
                        if (incrementSeconds > 0 && !isRunning) Text("+${incrementSeconds}s/siirto", color = whiteTextColor.copy(alpha = 0.8f), fontSize = 12.sp)
                        if (isPaused) Text("⏸ TAUKO", color = Color(0xFFE65100), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        if (!isRunning && activePlayer == null) Text("👇 PAINA ALOITTAaksesi", color = Color(0xFFE65100), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        } else {
            // Pystymoodi: pelaajat päällekkäin
            Column(modifier = Modifier.fillMaxSize()) {
                // Musta pelaaja (ylhäällä, käännetty)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .graphicsLayer(rotationZ = 180f)
                        .background(blackBg)
                        .clickable(enabled = isRunning && !isPaused && activePlayer == "black") {
                            vibrate()
                            viewModel.pressPlayer("black")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        Text("MUSTA", color = blackTextColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(formatTime(blackTime), color = blackTextColor, fontSize = 56.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("Siirrot: $blackMoves", color = blackTextColor, fontSize = 16.sp)
                        if (incrementSeconds > 0 && !isRunning) Text("+${incrementSeconds}s/siirto", color = blackTextColor.copy(alpha = 0.8f), fontSize = 12.sp)
                        if (isPaused) Text("⏸ TAUKO", color = Color.Yellow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
                
                Divider(color = Color.White, thickness = 2.dp, modifier = Modifier.fillMaxWidth().height(2.dp))
                
                // Valkoinen pelaaja (alhaalla)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(whiteBg)
                        .clickable { 
                            vibrate()
                            viewModel.pressPlayer("white")
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize().padding(8.dp)
                    ) {
                        Text("VALKOINEN", color = whiteTextColor, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(formatTime(whiteTime), color = whiteTextColor, fontSize = 56.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                        Text("Siirrot: $whiteMoves", color = whiteTextColor, fontSize = 16.sp)
                        if (incrementSeconds > 0 && !isRunning) Text("+${incrementSeconds}s/siirto", color = whiteTextColor.copy(alpha = 0.8f), fontSize = 12.sp)
                        if (isPaused) Text("⏸ TAUKO", color = Color(0xFFE65100), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        if (!isRunning && activePlayer == null) Text("👇 PAINA ALOITTAaksesi", color = Color(0xFFE65100), fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        
        // RESET-nappi
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(12.dp)
                .size(48.dp)
                .background(Color(0xFFF44336), shape = RoundedCornerShape(24.dp))
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            if (isRunning && !isPaused) viewModel.pauseGame()
                            viewModel.resetToTimeSelection()
                            vibrate()
                        }
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Color.White, modifier = Modifier.size(28.dp))
        }
        
        // ASETUKSET-nappi
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(12.dp)
                .size(48.dp)
                .background(Color(0xFF2196F3), shape = RoundedCornerShape(24.dp))
                .clickable {
                    if (isRunning && !isPaused) viewModel.pauseGame()
                    showSettingsDialog = true
                    vibrate()
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Settings, contentDescription = "Asetukset", tint = Color.White, modifier = Modifier.size(28.dp))
        }
        
        // JATKA-painike
        if (isPaused && isRunning) {
            Button(
                onClick = { viewModel.resumeGame() },
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800))
            ) {
                Text("▶ JATKA PELIÄ", fontSize = 20.sp, color = Color.White)
            }
        }
    }
    
    // Asetusdialogi
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text("Aikakontrollin valinta", fontSize = 20.sp) },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(timePresets) { preset ->
                        val isSelected = selectedMinutes == preset.minutes && selectedIncrement == preset.incrementSeconds
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable {
                                if (preset.name == "Custom") {
                                    showSettingsDialog = false
                                    showCustomDialog = true
                                } else {
                                    viewModel.selectTime(preset.minutes, preset.incrementSeconds)
                                    showSettingsDialog = false
                                }
                            },
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 2.dp),
                            colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White)
                        ) {
                            Row(Modifier.fillMaxWidth().padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(preset.name, fontSize = 16.sp)
                                if (preset.name != "Custom") {
                                    if (preset.incrementSeconds > 0) Text("${preset.minutes} min + ${preset.incrementSeconds}s", fontSize = 13.sp)
                                    else Text("${preset.minutes} min", fontSize = 13.sp)
                                } else {
                                    Text("⚙️ Aseta oma aika", fontSize = 13.sp, color = Color(0xFF2196F3))
                                }
                                if (isSelected && preset.name != "Custom") Text("✓", fontSize = 16.sp, color = Color(0xFF4CAF50))
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showSettingsDialog = false }) { Text("Peruuta") } }
        )
    }
    
    // Custom-aika dialogi
    if (showCustomDialog) {
        AlertDialog(
            onDismissRequest = { showCustomDialog = false },
            title = { Text("Aseta oma aika", fontSize = 20.sp) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Min", fontSize = 14.sp, color = Color.Gray)
                            Spacer(Modifier.height(4.dp))
                            Box(Modifier.fillMaxWidth().height(70.dp)) {
                                OutlinedTextField(
                                    value = customMinutes,
                                    onValueChange = { customMinutes = it.filter { it.isDigit() } },
                                    modifier = Modifier.fillMaxSize(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    isError = customMinutes.toIntOrNull() !in 1..60 && customMinutes.isNotEmpty(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                            }
                        }
                        Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Incr", fontSize = 14.sp, color = Color.Gray)
                            Spacer(Modifier.height(4.dp))
                            Box(Modifier.fillMaxWidth().height(70.dp)) {
                                OutlinedTextField(
                                    value = customIncrement,
                                    onValueChange = { customIncrement = it.filter { it.isDigit() } },
                                    modifier = Modifier.fillMaxSize(),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    isError = customIncrement.toIntOrNull() !in 0..60 && customIncrement.isNotEmpty(),
                                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = 28.sp, textAlign = TextAlign.Center),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = Color(0xFF4CAF50),
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                            }
                        }
                    }
                    
                    val minutesValid = customMinutes.toIntOrNull() in 1..60
                    val incrementValid = customIncrement.toIntOrNull() in 0..60
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = if (minutesValid && incrementValid) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (minutesValid && incrementValid) {
                                val inc = customIncrement.toIntOrNull() ?: 0
                                if (inc > 0) "${customMinutes} min + ${inc} s" else "${customMinutes} min"
                            } else "Syötä min 1-60, inc 0-60",
                            fontSize = 14.sp,
                            modifier = Modifier.padding(10.dp),
                            color = if (minutesValid && incrementValid) Color(0xFF2E7D32) else Color(0xFFC62828),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val minutes = customMinutes.toIntOrNull()
                        val increment = customIncrement.toIntOrNull()
                        if (minutes != null && minutes in 1..60 && increment != null && increment in 0..60) {
                            viewModel.selectTime(minutes, increment)
                            showCustomDialog = false
                        }
                    },
                    enabled = customMinutes.toIntOrNull() in 1..60 && customIncrement.toIntOrNull() in 0..60
                ) { Text("Aseta ja aloita", fontSize = 16.sp) }
            },
            dismissButton = { TextButton(onClick = { showCustomDialog = false }) { Text("Peruuta", fontSize = 16.sp) } }
        )
    }
}
