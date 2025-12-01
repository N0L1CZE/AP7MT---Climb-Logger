package cz.patrik.stanko.climbinglogger.ui.sessions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.patrik.stanko.climbinglogger.data.ServiceLocator
import cz.patrik.stanko.climbinglogger.data.local.ClimbSession
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SessionsViewModel : ViewModel() {
    private val repository = ServiceLocator.repository

    val sessions: StateFlow<List<ClimbSession>> =
        repository.getSessions()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addSession(date: String, location: String, isOutdoor: Boolean) {
        viewModelScope.launch {
            repository.addSession(date, location, isOutdoor)
        }
    }
}
