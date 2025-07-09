package com.meher.markbuddy

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.meher.markbuddy.ui.screens.*
import com.meher.markbuddy.ui.theme.LocalThemeSettings
import com.meher.markbuddy.ui.viewmodel.MarkBuddyViewModel
import org.koin.androidx.compose.koinViewModel
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings

@Composable
fun MarkBuddyApp() {
    val navController = rememberNavController()
    val viewModel: MarkBuddyViewModel = koinViewModel()
    val themeSettings = LocalThemeSettings.current

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "gpa",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("gpa") { GpaScreen(viewModel, navController) }
            composable("cgpa") { CgpaScreen(viewModel, navController) }
            composable("settings") { SettingsScreen(navController) }
            composable("course/{courseId}") { backStackEntry ->
                CourseDetailScreen(
                    viewModel,
                    courseId = backStackEntry.arguments?.getString("courseId")?.toLongOrNull() ?: 0,
                    navController
                )
            }
            composable("semester/{semesterId}") { backStackEntry ->
                SemesterDetailScreen(
                    viewModel,
                    semesterId = backStackEntry.arguments?.getString("semesterId")?.toLongOrNull() ?: 0,
                    navController
                )
            }
            composable("about") { AboutScreen(navController) }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("GPA", "gpa", Icons.Default.Info),
        BottomNavItem("CGPA", "cgpa", Icons.Default.List),
        BottomNavItem("Settings", "settings", Icons.Default.Settings)
    )
    NavigationBar {
        val currentRoute by navController.currentBackStackEntryAsState()
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute?.destination?.route == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val label: String, val route: String, val icon: ImageVector)