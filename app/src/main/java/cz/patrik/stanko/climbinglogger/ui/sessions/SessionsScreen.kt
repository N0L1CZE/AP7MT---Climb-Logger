package cz.patrik.stanko.climbinglogger.ui.sessions

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cz.patrik.stanko.climbinglogger.data.local.ClimbSession

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    onAddSession: () -> Unit,
    onEditSession: (Long) -> Unit,
    viewModel: SessionsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val filteredSessions = remember(uiState.sessions, uiState.filterQuery) {
        if (uiState.filterQuery.isBlank()) {
            uiState.sessions
        } else {
            val q = uiState.filterQuery.lowercase()
            uiState.sessions.filter { session ->
                session.title.lowercase().contains(q) ||
                        (session.notes?.lowercase()?.contains(q) == true) ||
                        (session.locationName?.lowercase()?.contains(q) == true)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Můj hiking log") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddSession) {
                Icon(Icons.Default.Add, contentDescription = "Přidat zápis")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.filterQuery,
                onValueChange = { viewModel.onFilterChange(it) },
                label = { Text("Filtrovat zápisy") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (filteredSessions.isEmpty()) {
                Text(
                    text = "Zatím nemáš žádné zápisy.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Klepni na + a přidej první hike.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(filteredSessions, key = { it.id }) { session ->
                        SessionCard(
                            session = session,
                            onClick = { onEditSession(session.id) },
                            onDelete = { viewModel.deleteSession(session) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SessionCard(
    session: ClimbSession,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.title,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    val subtitle = buildString {
                        append(session.date)
                        session.durationMinutes?.let { minutes ->
                            if (minutes > 0) {
                                append(" • ")
                                val hours = minutes / 60
                                val mins = minutes % 60
                                if (hours > 0) {
                                    append("${hours} h")
                                    if (mins > 0) append(" ${mins} min")
                                } else {
                                    append("$mins min")
                                }
                            }
                        }
                        session.locationName?.let { loc ->
                            if (loc.isNotBlank()) {
                                append(" • ")
                                append(loc)
                            }
                        }
                    }

                    if (subtitle.isNotBlank()) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Smazat")
                }
            }

            session.notes?.takeIf { it.isNotBlank() }?.let { notes ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notes,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
