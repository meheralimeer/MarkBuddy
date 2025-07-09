package com.meher.markbuddy.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meher.markbuddy.data.entity.Semester
import com.meher.markbuddy.ui.viewmodel.MarkBuddyViewModel
import kotlinx.coroutines.launch

@Composable
fun CgpaScreen(viewModel: MarkBuddyViewModel, navController: NavController) {
    val semesters by viewModel.getAllSemesters().collectAsState(initial = emptyList())
    val isLoading by viewModel.isLoading.collectAsState()
    var showAddSemesterDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "All Semesters", style = MaterialTheme.typography.headlineMedium)
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else {
            LazyColumn {
                items(semesters) { semester ->
                    SemesterItem(semester = semester, onClick = {
                        navController.navigate("semester/${semester.id}")
                    })
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showAddSemesterDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Semester")
        }
    }

    if (showAddSemesterDialog) {
        AddSemesterDialog(
            onDismiss = { showAddSemesterDialog = false },
            onAdd = { name ->
                coroutineScope.launch {
                    viewModel.addSemester(Semester(name = name))
                    showAddSemesterDialog = false
                }
            }
        )
    }
}

@Composable
fun SemesterItem(semester: Semester, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = semester.name,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "CGPA: ${semester.cgpa?.let { String.format("%.2f", it) } ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AddSemesterDialog(onDismiss: () -> Unit, onAdd: (String) -> Unit) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Semester") },
        text = {
            TextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Semester Name") }
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name)
                    }
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}