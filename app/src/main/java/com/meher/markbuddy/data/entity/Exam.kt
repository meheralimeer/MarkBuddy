package com.meher.markbuddy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exams")
data class Exam(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val type: ExamType,
    val obtainedMarks: Float,
    val totalMarks: Float
)

enum class ExamType {
    MIDTERM, TERMINAL, LAB_MIDTERM, LAB_TERMINAL
}