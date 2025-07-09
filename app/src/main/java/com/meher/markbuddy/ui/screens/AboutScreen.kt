package com.meher.markbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun AboutScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "About MarkBuddy", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Version: 1.0.0")
        Text(text = "Developed by: Your Name")
        Text(text = "A GPA/CGPA tracking app for students.")
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Links:")
        TextButton(onClick = { /* Open GitHub link */ }) {
            Text("GitHub Repository")
        }
        TextButton(onClick = { /* Open xAI link */ }) {
            Text("xAI Website")
        }
        Spacer(modifier = Modifier.weight(1f))
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
    }
}