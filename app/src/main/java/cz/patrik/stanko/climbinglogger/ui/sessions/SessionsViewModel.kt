package cz.patrik.stanko.climbinglogger.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.patrik.stanko.climbinglogger.data.ClimbingRepository
import cz.patrik.stanko.climbinglogger.data.ServiceLocator
import cz.patrik.stanko.climbinglogger.data.local.ClimbSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SessionsUiState(
    val sessions: List<ClimbSession> = emptyList(),
    val filterQuery: String = ""
)

class SessionsViewModel : ViewModel() {

    private val repository: ClimbingRepository = ServiceLocator.repository

    private val _uiState = MutableStateFlow(SessionsUiState())
    val uiState: StateFlow<SessionsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getSessions().collect { list ->
                _uiState.update { it.copy(sessions = list) }
            }
        }
    }

    fun onFilterChange(query: String) {
        _uiState.update { it.copy(filterQuery = query) }
    }

    fun deleteSession(session: ClimbSession) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }
}
