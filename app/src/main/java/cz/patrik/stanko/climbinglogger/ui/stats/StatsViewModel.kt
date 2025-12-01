package cz.patrik.stanko.climbinglogger.ui.stats

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.patrik.stanko.climbinglogger.data.ServiceLocator
import cz.patrik.stanko.climbinglogger.data.local.ClimbSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class StatsUiState(
    val totalSessions: Int = 0,
    val averageDurationMinutes: Int? = null,
    val lastSessionTitle: String? = null,
    val lastSessionDate: String? = null
)

class StatsViewModel : ViewModel() {

    private val repository = ServiceLocator.repository

    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState: StateFlow<StatsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSessions().collectLatest { sessions ->
                _uiState.value = computeStats(sessions)
            }
        }
    }

    private fun computeStats(sessions: List<ClimbSession>): StatsUiState {
        if (sessions.isEmpty()) return StatsUiState()

        val total = sessions.size
        val durations = sessions.mapNotNull { it.durationMinutes }
        val avgDuration = if (durations.isNotEmpty()) {
            durations.sum() / durations.size
        } else null

        val last = sessions.maxByOrNull { it.date }

        return StatsUiState(
            totalSessions = total,
            averageDurationMinutes = avgDuration,
            lastSessionTitle = last?.title,
            lastSessionDate = last?.date
        )
    }
}
