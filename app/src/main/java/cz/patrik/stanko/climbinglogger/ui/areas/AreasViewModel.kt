package cz.patrik.stanko.climbinglogger.ui.areas

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import cz.patrik.stanko.climbinglogger.data.ServiceLocator
import cz.patrik.stanko.climbinglogger.data.remote.ClimbingAreaDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AreasUiState(
    val isLoading: Boolean = false,
    val areas: List<ClimbingAreaDto> = emptyList(),
    val error: String? = null
)

/**
 * ViewModel pro obrazovku s hiking trasami z TrailAPI.
 */
class AreasViewModel : ViewModel() {
    private val repository = ServiceLocator.repository

    private val _uiState = MutableStateFlow(AreasUiState())
    val uiState: StateFlow<AreasUiState> = _uiState.asStateFlow()

    /**
     * Načte oblasti pro dané souřadnice.
     */
    fun loadAreas(lat: Double, lon: Double, state: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val data = repository.getAreas(lat, lon, state)
                _uiState.value = AreasUiState(
                    isLoading = false,
                    areas = data,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = AreasUiState(
                    isLoading = false,
                    areas = emptyList(),
                    error = e.message ?: "Neznámá chyba"
                )
            }
        }
    }
}
