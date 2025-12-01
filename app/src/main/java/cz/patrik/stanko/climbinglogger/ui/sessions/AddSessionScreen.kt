package cz.patrik.stanko.climbinglogger.ui.sessions

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cz.patrik.stanko.climbinglogger.data.ServiceLocator
import cz.patrik.stanko.climbinglogger.data.local.ClimbSession
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionScreen(
    sessionId: Long? = null,
    onSessionSaved: () -> Unit
) {
    val repository = ServiceLocator.repository
    val scope = rememberCoroutineScope()

    var title by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") } // uživatel napíše ručně (např. 2025-12-01)
    var hoursText by rememberSaveable { mutableStateOf("") }
    var minutesText by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var locationName by rememberSaveable { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }

    // Pokud editujeme existující session, načteme data
    LaunchedEffect(sessionId) {
        if (sessionId != null) {
            val existing: ClimbSession? = repository.getSession(sessionId)
            if (existing != null) {
                title = existing.title
                date = existing.date
                notes = existing.notes.orEmpty()
                locationName = existing.locationName.orEmpty()
                photoUri = existing.photoUri
                existing.durationMinutes?.let { totalMinutes ->
                    val h = totalMinutes / 60
                    val m = totalMinutes % 60
                    hoursText = if (h > 0) h.toString() else ""
                    minutesText = if (m > 0) m.toString() else ""
                }
            }
        }
    }

    // Výběr fotky z galerie (např. screenshot ze Stravy)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri?.toString()
    }

    fun saveSession() {
        scope.launch {
            val h = hoursText.toIntOrNull() ?: 0
            val m = minutesText.toIntOrNull() ?: 0
            val totalMinutes = h * 60 + m
            val durationValue = if (totalMinutes > 0) totalMinutes else null

            if (sessionId == null) {
                // nový zápis
                repository.addSession(
                    title = title.ifBlank { "Bez názvu" },
                    date = date.ifBlank { "neznámé datum" },
                    durationMinutes = durationValue,
                    notes = notes.ifBlank { null },
                    photoUri = photoUri,
                    locationName = locationName.ifBlank { null }
                )
            } else {
                // update existujícího
                val existing = repository.getSession(sessionId)
                if (existing != null) {
                    val updated = existing.copy(
                        title = title.ifBlank { "Bez názvu" },
                        date = date.ifBlank { existing.date },
                        durationMinutes = durationValue,
                        notes = notes.ifBlank { null },
                        photoUri = photoUri,
                        locationName = locationName.ifBlank { null }
                    )
                    repository.updateSession(updated)
                }
            }

            onSessionSaved()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (sessionId == null) "Nový zápis" else "Upravit zápis") },
                navigationIcon = {
                    IconButton(onClick = onSessionSaved) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Zpět"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Název túry") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text("Datum (např. 2025-12-01)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = hoursText,
                    onValueChange = { hoursText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Hodiny") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 4.dp)
                )
                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it.filter { ch -> ch.isDigit() } },
                    label = { Text("Minuty") },
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Lokalita (např. z TrailAPI)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Poznámky") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                    Icon(Icons.Filled.Image, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Přidat fotku")
                }
            }

            photoUri?.let { uriString ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = uriString,
                    contentDescription = "Foto z túry",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { saveSession() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (sessionId == null) "Uložit zápis" else "Uložit změny")
            }
        }
    }
}
