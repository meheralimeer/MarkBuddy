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
import com.meher.markbuddy.data.entity.Course
import com.meher.markbuddy.ui.viewmodel.MarkBuddyViewModel
import kotlinx.coroutines.launch

@Composable
fun GpaScreen(viewModel: MarkBuddyViewModel, navController: NavController) {
    val currentSemester by viewModel.currentSemester.collectAsState()
    val courses by viewModel.getCoursesForSemester(currentSemester?.id ?: 0).collectAsState(initial = emptyList())
    val cgpa by viewModel.cgpa.collectAsState()
    var showAddCourseDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Current Semester: ${currentSemester?.name ?: "N/A"}",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Semester GPA: ${currentSemester?.cgpa?.let { String.format("%.2f", it) } ?: "N/A"}",
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = "Overall CGPA: ${cgpa?.let { String.format("%.2f", it) } ?: "N/A"}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn {
            items(courses) { course ->
                CourseItem(course = course, onClick = {
                    navController.navigate("course/${course.id}")
                })
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = { showAddCourseDialog = true },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Add Course")
        }
    }

    if (showAddCourseDialog) {
        AddCourseDialog(
            onDismiss = { showAddCourseDialog = false },
            onAdd = { name, creditHours, hasLab, quickGpa ->
                coroutineScope.launch {
                    viewModel.addCourse(
                        Course(
                            semesterId = currentSemester?.id ?: 0,
                            name = name,
                            creditHours = creditHours,
                            hasLab = hasLab,
                            gpa = quickGpa
                        )
                    )
                    showAddCourseDialog = false
                }
            }
        )
    }
}

@Composable
fun CourseItem(course: Course, onClick: () -> Unit) {
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
            Column(modifier = Modifier.weight(1f)) {
                Text(text = course.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Credits: ${course.creditHours}", style = MaterialTheme.typography.bodyMedium)
            }
            Text(
                text = "GPA: ${course.gpa?.let { String.format("%.2f", it) } ?: "N/A"}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun AddCourseDialog(onDismiss: () -> Unit, onAdd: (String, Float, Boolean, Float?) -> Unit) {
    var name by remember { mutableStateOf("") }
    var creditHours by remember { mutableStateOf("") }
    var hasLab by remember { mutableStateOf(false) }
    var isQuickEntry by remember { mutableStateOf(true) }
    var quickGpa by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Course") },
        text = {
            Column {
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Course Name") }
                )
                TextField(
                    value = creditHours,
                    onValueChange = { creditHours = it },
                    label = { Text("Credit Hours") }
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = hasLab, onCheckedChange = { hasLab = it })
                    Text("Has Lab")
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = isQuickEntry, onCheckedChange = { isQuickEntry = it })
                    Text("Quick Entry")
                }
                if (isQuickEntry) {
                    TextField(
                        value = quickGpa,
                        onValueChange = { quickGpa = it },
                        label = { Text("GPA") }
                    )
                } else {
                    Text("Detailed entry will navigate to course details after adding.")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val credits = creditHours.toFloatOrNull() ?: 0f
                    val gpa = if (isQuickEntry) quickGpa.toFloatOrNull() else null
                    if (name.isNotBlank() && credits > 0) {
                        onAdd(name, credits, hasLab, gpa)
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