package cz.patrik.stanko.climbinglogger.ui.areas

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Obrazovka, která ukazuje hiking trasy z TrailAPI.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AreasScreen(
    onBack: () -> Unit,
    viewModel: AreasViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadAreas()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hiking trasy (TrailAPI)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Zpět")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
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
                    LazyColumn {
                        items(uiState.areas) { area ->
                            Column(modifier = Modifier.padding(vertical = 8.dp)) {
                                Text(
                                    text = area.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                if (area.location.isNotBlank()) {
                                    Text(
                                        text = area.location,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                area.description?.let {
                                    if (it.isNotBlank()) {
                                        Text(
                                            text = it,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
