package cz.patrik.stanko.climbinglogger.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cz.patrik.stanko.climbinglogger.ui.areas.AreasScreen
import cz.patrik.stanko.climbinglogger.ui.home.HomeScreen
import cz.patrik.stanko.climbinglogger.ui.sessions.AddSessionScreen
import cz.patrik.stanko.climbinglogger.ui.sessions.SessionsScreen

object Destinations {
    const val HOME = "home"
    const val SESSIONS = "sessions"
    const val ADD_SESSION = "add_session"
    const val AREAS = "areas"
}

@Composable
fun ClimbingLoggerApp() {
    val navController = rememberNavController()

    Surface(color = MaterialTheme.colorScheme.background) {
        NavHost(
            navController = navController,
            startDestination = Destinations.HOME
        ) {
            composable(Destinations.HOME) {
                HomeScreen(
                    onShowSessions = { navController.navigate(Destinations.SESSIONS) },
                    onShowAreas = { navController.navigate(Destinations.AREAS) }
                )
            }
            composable(Destinations.SESSIONS) {
                SessionsScreen(
                    onBack = { navController.popBackStack() },
                    onAddSession = { navController.navigate(Destinations.ADD_SESSION) }
                )
            }
            composable(Destinations.ADD_SESSION) {
                AddSessionScreen(
                    onSessionSaved = { navController.popBackStack() }
                )
            }
            composable(Destinations.AREAS) {
                AreasScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
