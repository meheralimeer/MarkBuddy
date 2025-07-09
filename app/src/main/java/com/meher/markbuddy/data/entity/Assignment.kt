package com.meher.markbuddy.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assignments")
data class Assignment(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val courseId: Long,
    val obtainedMarks: Float,
    val totalMarks: Float,
    val isLab: Boolean
)