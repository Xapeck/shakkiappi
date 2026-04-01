package com.example.REMOVED.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.REMOVED.data.local.datastore.SettingsDataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(private val settings: SettingsDataStore) : ViewModel() {
    val theme = settings.themeFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "SYSTEM")
    val clockStyle = settings.clockStyleFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "DIGITAL")
    val soundEnabled = settings.soundFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    val vibrationEnabled = settings.vibrationFlow.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    fun updateTheme(v: String) = viewModelScope.launch { settings.saveTheme(v) }
    fun updateClockStyle(v: String) = viewModelScope.launch { settings.saveClockStyle(v) }
    fun updateSoundEnabled(v: Boolean) = viewModelScope.launch { settings.saveSound(v) }
    fun updateVibrationEnabled(v: Boolean) = viewModelScope.launch { settings.saveVibration(v) }
}
