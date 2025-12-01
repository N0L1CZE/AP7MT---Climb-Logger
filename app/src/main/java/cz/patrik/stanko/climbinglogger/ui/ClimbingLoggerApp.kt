package cz.patrik.stanko.climbinglogger.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import cz.patrik.stanko.climbinglogger.ui.areas.AreasScreen
import cz.patrik.stanko.climbinglogger.ui.sessions.AddSessionScreen
import cz.patrik.stanko.climbinglogger.ui.sessions.SessionsScreen
import cz.patrik.stanko.climbinglogger.ui.stats.StatsScreen

object Destinations {
    const val LOG = "log"
    const val ADD_SESSION = "add_session"
    const val EDIT_SESSION = "edit_session"
    const val EXPLORE = "explore"
    const val STATS = "stats"
}

data class BottomNavItem(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@Composable
fun ClimbingLoggerApp() {
    val navController = rememberNavController()

    val bottomItems = listOf(
        BottomNavItem(Destinations.LOG, "Log", Icons.AutoMirrored.Filled.List),
        BottomNavItem(Destinations.EXPLORE, "Trasy", Icons.Filled.Map),
        BottomNavItem(Destinations.STATS, "Statistiky", Icons.Filled.ShowChart),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomItems.forEach { item ->
                    val selected = currentDestination.isInHierarchy(item.route)

                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = { Text(item.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Destinations.LOG,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Destinations.LOG) {
                SessionsScreen(
                    onAddSession = { navController.navigate(Destinations.ADD_SESSION) },
                    onEditSession = { id ->
                        navController.navigate("${Destinations.EDIT_SESSION}/$id")
                    }
                )
            }
            composable(Destinations.ADD_SESSION) {
                AddSessionScreen(
                    sessionId = null,
                    onSessionSaved = { navController.popBackStack() }
                )
            }
            composable(
                route = "${Destinations.EDIT_SESSION}/{sessionId}",
                arguments = listOf(
                    navArgument("sessionId") { type = NavType.LongType }
                )
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getLong("sessionId")
                AddSessionScreen(
                    sessionId = id,
                    onSessionSaved = { navController.popBackStack() }
                )
            }
            composable(Destinations.EXPLORE) {
                AreasScreen()
            }
            composable(Destinations.STATS) {
                StatsScreen()
            }
        }
    }
}

private fun NavDestination?.isInHierarchy(route: String): Boolean {
    return this?.hierarchy?.any { it.route == route } == true
}
