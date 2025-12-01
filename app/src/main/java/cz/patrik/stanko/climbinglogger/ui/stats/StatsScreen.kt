package cz.patrik.stanko.climbinglogger.ui.stats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: StatsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Statistiky") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            StatCard(
                title = "Celkem výletů",
                value = uiState.totalSessions.toString()
            )

            val avgText = uiState.averageDurationMinutes?.let { minutes ->
                val h = minutes / 60
                val m = minutes % 60
                if (h > 0) {
                    if (m > 0) "${h} h ${m} min" else "${h} h"
                } else {
                    "$m min"
                }
            } ?: "—"

            StatCard(
                title = "Průměrná délka výletu",
                value = avgText
            )

            val lastText = if (uiState.lastSessionTitle != null && uiState.lastSessionDate != null) {
                "${uiState.lastSessionTitle} (${uiState.lastSessionDate})"
            } else {
                "—"
            }

            StatCard(
                title = "Poslední výlet",
                value = lastText
            )
        }
    }
}

@Composable
private fun StatCard(
    title: String,
    value: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
