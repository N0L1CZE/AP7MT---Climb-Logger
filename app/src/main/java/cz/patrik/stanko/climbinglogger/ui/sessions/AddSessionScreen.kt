package cz.patrik.stanko.climbinglogger.ui.sessions

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import cz.patrik.stanko.climbinglogger.data.ServiceLocator
import cz.patrik.stanko.climbinglogger.data.local.ClimbSession
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddSessionScreen(
    sessionId: Long? = null,
    onSessionSaved: () -> Unit
) {
    val repository = ServiceLocator.repository
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var title by rememberSaveable { mutableStateOf("") }
    var date by rememberSaveable { mutableStateOf("") }          // ukládáme jako "YYYY-MM-DD"
    var durationMinutes by rememberSaveable { mutableStateOf<Int?>(null) }
    var notes by rememberSaveable { mutableStateOf("") }
    var locationName by rememberSaveable { mutableStateOf("") }
    var photoUri by rememberSaveable { mutableStateOf<String?>(null) }

    // EDIT režim – načteme existující data
    LaunchedEffect(sessionId) {
        if (sessionId != null) {
            val existing: ClimbSession? = repository.getSession(sessionId)
            if (existing != null) {
                title = existing.title
                date = existing.date
                notes = existing.notes.orEmpty()
                locationName = existing.locationName.orEmpty()
                photoUri = existing.photoUri
                durationMinutes = existing.durationMinutes
            }
        }
    }

    // výběr fotky (třeba screenshot ze Stravy)
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        photoUri = uri?.toString()
    }

    // --- helpery pro datum a délku ---

    fun formatDuration(minutes: Int?): String {
        if (minutes == null || minutes <= 0) return "Nenastaveno"
        val h = minutes / 60
        val m = minutes % 60
        return buildString {
            if (h > 0) {
                append(h).append(" h")
                if (m > 0) append(" ").append(m).append(" min")
            } else {
                append(m).append(" min")
            }
        }
    }

    fun openDurationPicker() {
        val current = durationMinutes ?: 0
        val initialHour = current / 60
        val initialMinute = current % 60

        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val total = hourOfDay * 60 + minute
                durationMinutes = if (total > 0) total else null
            },
            initialHour,
            initialMinute,
            true
        ).show()
    }

    fun openDatePicker() {
        val cal = Calendar.getInstance()

        // pokud máme datum ve formátu YYYY-MM-DD, zkusíme ho předvyplnit
        if (date.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))) {
            try {
                val year = date.substring(0, 4).toInt()
                val month = date.substring(5, 7).toInt() - 1
                val day = date.substring(8, 10).toInt()
                cal.set(year, month, day)
            } catch (_: Exception) {
            }
        }

        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                // uložíme jako YYYY-MM-DD
                date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    fun saveSession() {
        scope.launch {
            val durationValue = durationMinutes?.takeIf { it > 0 }

            if (sessionId == null) {
                repository.addSession(
                    title = title.ifBlank { "Bez názvu" },
                    date = date.ifBlank { "neznámé datum" },
                    durationMinutes = durationValue,
                    notes = notes.ifBlank { null },
                    photoUri = photoUri,
                    locationName = locationName.ifBlank { null }
                )
            } else {
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
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Název túry") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Datum – vybírá se přes DatePickerDialog
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Datum")
                    Text(text = if (date.isBlank()) "Nenastaveno" else date)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { openDatePicker() }) {
                    Text("Vybrat datum")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Délka výletu – TimePickerDialog
            Row(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Délka výletu")
                    Text(text = formatDuration(durationMinutes))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = { openDurationPicker() }) {
                    Text("Nastavit")
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = locationName,
                onValueChange = { locationName = it },
                label = { Text("Lokalita") },
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
