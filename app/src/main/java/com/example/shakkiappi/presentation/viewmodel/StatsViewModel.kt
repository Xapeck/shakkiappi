package com.example.shakkiappi.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shakkiappi.data.local.repository.GameRepository
import com.example.shakkiappi.data.model.Game
import com.example.shakkiappi.data.model.GameStats
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class StatsViewModel @Inject constructor(private val repo: GameRepository) : ViewModel() {
    val stats: StateFlow<GameStats> = repo.getStats().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GameStats())
    val games: StateFlow<List<Game>> = repo.getAllGames().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
