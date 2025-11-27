package cz.patrik.stanko.climbinglogger.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import cz.patrik.stanko.climbinglogger.ui.theme.ClimbingLoggerTheme

private enum class AppScreen {
    HOME,
    SESSIONS
}

@Composable
fun ClimbingLoggerApp() {
    var currentScreen by remember { mutableStateOf(AppScreen.HOME) }

    when (currentScreen) {
        AppScreen.HOME -> HomeScreen(
            onShowSessions = { currentScreen = AppScreen.SESSIONS }
        )
        AppScreen.SESSIONS -> SessionsScreen(
            onBackToHome = { currentScreen = AppScreen.HOME }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onShowSessions: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "ClimbingLogger") }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Vítej v ClimbingLoggeru 👋",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "Tady později uvidíš přehled svých lezeckých session a statistik.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onShowSessions) {
                Text("Moje session")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(
    onBackToHome: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Moje session") }
                // později sem přidáme zpětné tlačítko, až budeš chtít
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = "Zatím tady budou jen statické data.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBackToHome) {
                Text("Zpět na úvod")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ClimbingLoggerAppPreview() {
    ClimbingLoggerTheme {
        ClimbingLoggerApp()
    }
}
