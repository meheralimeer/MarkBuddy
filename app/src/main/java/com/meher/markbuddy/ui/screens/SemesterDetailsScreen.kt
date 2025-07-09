package com.meher.markbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meher.markbuddy.ui.viewmodel.MarkBuddyViewModel
import kotlinx.coroutines.launch

@Composable
fun SemesterDetailScreen(viewModel: MarkBuddyViewModel, semesterId: Long, navController: NavController) {
    val semester by viewModel.getSemester(semesterId).collectAsState(initial = null)
    val courses by viewModel.getCoursesForSemester(semesterId).collectAsState(initial = emptyList())
    var showEditSemesterDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    semester?.let { s ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = s.name, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "CGPA: ${s.cgpa?.let { String.format("%.2f", it) } ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                items(courses) { course ->
                    CourseItem(course = course, onClick = {
                        navController.navigate("course/${course.id}")
                    })

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(onClick = { showEditSemesterDialog = true }) {
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        coroutineScope.launch {
                            viewModel.deleteSemester(s)
                            navController.popBackStack()
                        }
                    }) {
                        Text("Delete Semester")
                    }
                }
            }}

            if (showEditSemesterDialog) {
                EditSemesterDialog(
                    currentName = s.name,
                    onDismiss = { showEditSemesterDialog = false },
                    onSave = { name ->
                        coroutineScope.launch {
                            viewModel.updateSemester(s.copy(name = name))
                            showEditSemesterDialog = false
                        }
                    }
                )
            }
        } ?: Text("Semester not found", modifier = Modifier.padding(16.dp))
    }
}

@Composable
fun EditSemesterDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Semester") },
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
                        onSave(name)
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text("Cancel") }
        }
    )
}