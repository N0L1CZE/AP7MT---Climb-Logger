package cz.patrik.stanko.climbinglogger.ui.areas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreasScreen(
    viewModel: AreasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Výchozí hodnoty – můžeš si změnit na co chceš
    var latText by rememberSaveable { mutableStateOf("37.7749") }      // San Francisco
    var lonText by rememberSaveable { mutableStateOf("-122.4194") }
    var stateText by rememberSaveable { mutableStateOf("California") }

    var localError by remember { mutableStateOf<String?>(null) }

    // Při prvním zobrazení načteme defaultní oblast
    LaunchedEffect(Unit) {
        val lat = latText.toDoubleOrNull()
        val lon = lonText.toDoubleOrNull()
        if (lat != null && lon != null) {
            viewModel.loadAreas(lat, lon, stateText.ifBlank { null })
        } else {
            localError = "Výchozí souřadnice jsou neplatné."
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hiking trasy (TrailAPI)") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // --- část pro výběr místa ---

            Text(
                text = "Zadej souřadnice místa, kde chceš najít trasy:",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = latText,
                onValueChange = { latText = it },
                label = { Text("Latitude (šířka)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = lonText,
                onValueChange = { lonText = it },
                label = { Text("Longitude (délka)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))

            OutlinedTextField(
                value = stateText,
                onValueChange = { stateText = it },
                label = { Text("State (volitelné, např. California)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val lat = latText.toDoubleOrNull()
                    val lon = lonText.toDoubleOrNull()
                    if (lat == null || lon == null) {
                        localError = "Latitude a longitude musí být čísla."
                    } else {
                        localError = null
                        viewModel.loadAreas(lat, lon, stateText.ifBlank { null })
                    }
                }
            ) {
                Text("Načíst trasy")
            }

            localError?.let { msg ->
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = msg,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- seznam tras ---

            when {
                uiState.isLoading -> {
                    CircularProgressIndicator()
                }

                uiState.error != null -> {
                    Text(
                        text = "Chyba při načítání: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        items(uiState.areas) { area ->
                            TrailCard(
                                name = area.name,
                                location = area.location,
                                description = area.description
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrailCard(
    name: String,
    location: String,
    description: String?
) {
    Card(
        modifier = Modifier
            .padding(vertical = 6.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium
            )
            if (location.isNotBlank()) {
                Text(
                    text = location,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            if (!description.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
