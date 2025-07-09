//package com.meher.markbuddy.ui.screens
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.background
//import androidx.compose.foundation.gestures.detectTapGestures
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.AlertDialog
//import androidx.compose.material3.Card
//import androidx.compose.material3.CardDefaults
//import androidx.compose.material3.CardDefaults.cardElevation
//import androidx.compose.material3.ExtendedFloatingActionButton
//import androidx.compose.material3.Icon
//import androidx.compose.material3.LinearProgressIndicator
//import androidx.compose.material3.MaterialTheme
//import androidx.compose.material3.Text
//import androidx.compose.material3.TextButton
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.collectAsState
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.input.pointer.pointerInput
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import com.meher.markbuddy.data.entity.Course
//import com.meher.markbuddy.ui.viewmodel.MarkBuddyViewModel
//import kotlinx.coroutines.launch
//
//@SuppressLint("DefaultLocale")
//@Composable
//fun HomeScreen(
//    viewModel: MarkBuddyViewModel,
//    onCourseClick: (Long) -> Unit,
//    onAddCourse: () -> Unit
//) {
//    val courses = viewModel.courses.collectAsState(initial = emptyList())
//    val cgpa = viewModel.calculateCGPA(courses.value)
//    val coroutineScope = rememberCoroutineScope()
//    var courseToDelete by remember { mutableStateOf<Course?>(null) }
//
//    val primaryColor = MaterialTheme.colorScheme.primary
//    val bgColor = MaterialTheme.colorScheme.background
//    val textColor = MaterialTheme.colorScheme.onBackground
//    val containerColor = MaterialTheme.colorScheme.surface
//
//    // Deletion confirmation dialog
//    courseToDelete?.let { course ->
//        AlertDialog(
//            onDismissRequest = { courseToDelete = null },
//            title = { Text("Delete Course") },
//            text = { Text("Are you sure you want to delete ${course.name}? This will also delete all associated assignments and quizzes.") },
//            confirmButton = {
//                TextButton(
//                    onClick = {
//                        coroutineScope.launch {
//                            viewModel.deleteCourseWithComponents(course)
//                            courseToDelete = null
//                        }
//                    }
//                ) {
//                    Text("Delete")
//                }
//            },
//            dismissButton = {
//                TextButton(onClick = { courseToDelete = null }) {
//                    Text("Cancel")
//                }
//            }
//        )
//    }
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(bgColor)
//            .padding(16.dp)
//    ) {
//        // Header
//        Text(
//            text = "Semester Snap",
//            color = textColor,
//            fontSize = 24.sp,
//            fontWeight = FontWeight.Bold
//        )
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // CGPA Card styled like Samsung UI
//        Card(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(vertical = 8.dp),
//            colors = CardDefaults.cardColors(containerColor = containerColor),
//            elevation = cardElevation(6.dp)
//        ) {
//            Column(modifier = Modifier.padding(16.dp)) {
//                Text(
//                    text = "Your CGPA",
//                    color = textColor,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold
//                )
//                Spacer(modifier = Modifier.height(4.dp))
//                Text(
//                    text = String.format("%.2f / 4.0", cgpa),
//                    color = textColor,
//                    fontSize = 16.sp
//                )
//                Spacer(modifier = Modifier.height(8.dp))
//                LinearProgressIndicator(
//                progress = { (cgpa / 4f).coerceIn(0f, 1f) },
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(8.dp)
//                    .clip(RoundedCornerShape(8.dp)),
//                color = primaryColor,
//                trackColor = Color.Gray,
//                )
//            }
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Course Cards
//        LazyColumn {
//            items(courses.value) { course ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(vertical = 4.dp)
//                        .pointerInput(Unit) {
//                            detectTapGestures(
//                                onTap = { onCourseClick(course.id) },
//                                onLongPress = { courseToDelete = course }
//                            )
//                        },
////                    colors = CardDefaults.cardColors(containerColor = containerColor),
//                    shape = RoundedCornerShape(12.dp),
//                    elevation = cardElevation(4.dp)
//                ) {
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(16.dp),
//                        horizontalArrangement = Arrangement.SpaceBetween
//                    ) {
//                        Text(
//                            course.name,
//                            color = textColor,
//                            style = MaterialTheme.typography.titleMedium
//                        )
//                        Text(
//                            text = String.format("GPA: %.2f", viewModel.calculateGPA(course.id)),
//                            color = textColor,
//                            style = MaterialTheme.typography.bodyMedium
//                        )
//                    }
//                }
//            }
//        }
//
//        // FAB
//        Box(
//            modifier = Modifier.fillMaxSize(),
//            contentAlignment = Alignment.BottomEnd
//        ) {
//            ExtendedFloatingActionButton(
//                onClick = onAddCourse,
//                containerColor = primaryColor,
//                contentColor = Color.White,
//                modifier = Modifier
//                    .padding(16.dp)
//                    .clip(RoundedCornerShape(16.dp))
//            ) {
//                Icon(Icons.Default.Add, contentDescription = "Add Course")
//            }
//        }
//    }
//}