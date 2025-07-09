package com.meher.markbuddy.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.meher.markbuddy.data.entity.*
import com.meher.markbuddy.ui.viewmodel.MarkBuddyViewModel
import kotlinx.coroutines.launch

@Composable
fun CourseDetailScreen(viewModel: MarkBuddyViewModel, courseId: Long, navController: NavController) {
    val course by viewModel.getCourse(courseId).collectAsState(initial = null)
    val quizzes by viewModel.getQuizzesForCourse(courseId).collectAsState(initial = emptyList())
    val assignments by viewModel.getAssignmentsForCourse(courseId).collectAsState(initial = emptyList())
    val exams by viewModel.getExamsForCourse(courseId).collectAsState(initial = emptyList())
    var showAddComponentDialog by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    course?.let { c ->
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = c.name, style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "GPA: ${c.gpa?.let { String.format("%.2f", it) } ?: "N/A"}",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "Credits: ${c.creditHours}",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                item {
                    Text("Quizzes", style = MaterialTheme.typography.titleMedium)
                }
                items(quizzes) { quiz ->
                    ComponentItem("Quiz", quiz.obtainedMarks, quiz.totalMarks)
                }
                item {
                    Text("Assignments", style = MaterialTheme.typography.titleMedium)
                }
                items(assignments) { assignment ->
                    ComponentItem(
                        if (assignment.isLab) "Lab Assignment" else "Assignment",
                        assignment.obtainedMarks,
                        assignment.totalMarks
                    )
                }
                item {
                    Text("Exams", style = MaterialTheme.typography.titleMedium)
                }
                items(exams) { exam ->
                    ComponentItem(exam.type.name, exam.obtainedMarks, exam.totalMarks)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(onClick = { showAddComponentDialog = true }) {
                    Text("Add Component")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    coroutineScope.launch {
                        viewModel.deleteCourse(c)
                        navController.popBackStack()
                    }
                }) {
                    Text("Delete Course")
                }
            }
        }

        if (showAddComponentDialog) {
            AddComponentDialog(
                onDismiss = { showAddComponentDialog = false },
                onAdd = { type, obtainedMarks, totalMarks ->
                    coroutineScope.launch {
                        when (type) {
                            "Quiz" -> viewModel.addQuiz(Quiz(courseId = courseId, obtainedMarks = obtainedMarks, totalMarks = totalMarks))
                            "Assignment" -> viewModel.addAssignment(Assignment(courseId = courseId, obtainedMarks = obtainedMarks, totalMarks = totalMarks, isLab = false))
                            "Lab Assignment" -> viewModel.addAssignment(Assignment(courseId = courseId, obtainedMarks = obtainedMarks, totalMarks = totalMarks, isLab = true))
                            else -> viewModel.addExam(Exam(courseId = courseId, type = ExamType.valueOf(type.uppercase()), obtainedMarks = obtainedMarks, totalMarks = totalMarks))
                        }
                        showAddComponentDialog = false
                    }
                },
                hasLab = c.hasLab
            )
        }
    } ?: Text("Course not found", modifier = Modifier.padding(16.dp))
}

@Composable
fun ComponentItem(name: String, obtainedMarks: Float, totalMarks: Float) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Text(text = name, modifier = Modifier.weight(1f))
            Text(text = "${obtainedMarks}/${totalMarks}")
        }
    }
}

@Composable
fun AddComponentDialog(
    onDismiss: () -> Unit,
    onAdd: (String, Float, Float) -> Unit,
    hasLab: Boolean
) {
    var type by remember { mutableStateOf("Quiz") }
    var obtainedMarks by remember { mutableStateOf("") }
    var totalMarks by remember { mutableStateOf("") }

    val componentTypes = buildList {
        addAll(listOf("Quiz", "Assignment"))
        if (hasLab) add("Lab Assignment")
        addAll(listOf("Midterm", "Terminal"))
        if (hasLab) addAll(listOf("Lab Midterm", "Lab Terminal"))
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Component") },
        text = {
            Column {
                DropdownMenu(
                    options = componentTypes,
                    selectedOption = type,
                    onOptionSelected = { type = it }
                )
                TextField(
                    value = obtainedMarks,
                    onValueChange = { obtainedMarks = it },
                    label = { Text("ObtainedMarks") }
                )
                TextField(
                    value = totalMarks,
                    onValueChange = { totalMarks = it },
                    label = { Text("TotalMarks") }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val obtained = obtainedMarks.toFloatOrNull() ?: 0f
                    val total = totalMarks.toFloatOrNull() ?: 0f
                    if (total > 0f) {
                        onAdd(type, obtained, total)
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