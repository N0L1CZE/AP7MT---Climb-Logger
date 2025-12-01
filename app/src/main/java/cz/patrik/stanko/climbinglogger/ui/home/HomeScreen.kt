package cz.patrik.stanko.climbinglogger.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onShowSessions: () -> Unit,
    onShowAreas: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("ClimbingLogger") })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(text = "Vítej v ClimbingLoggeru 👋")
            Spacer(Modifier.height(16.dp))
            Button(onClick = onShowSessions) {
                Text("Moje session")
            }
            Spacer(Modifier.height(8.dp))
            Button(onClick = onShowAreas) {
                Text("Lezecké oblasti (API)")
            }
        }
    }
}
