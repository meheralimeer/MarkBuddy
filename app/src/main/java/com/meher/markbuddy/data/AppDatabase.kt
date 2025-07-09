package com.meher.markbuddy.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.meher.markbuddy.data.dao.AssignmentDao
import com.meher.markbuddy.data.dao.CourseDao
import com.meher.markbuddy.data.dao.ExamDao
import com.meher.markbuddy.data.dao.QuizDao
import com.meher.markbuddy.data.dao.SemesterDao
import com.meher.markbuddy.data.entity.Assignment
import com.meher.markbuddy.data.entity.Course
import com.meher.markbuddy.data.entity.Exam
import com.meher.markbuddy.data.entity.Quiz
import com.meher.markbuddy.data.entity.Semester

@Database(
    entities = [Semester::class, Course::class, Assignment::class, Quiz::class, Exam::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun semesterDao(): SemesterDao
    abstract fun courseDao(): CourseDao
    abstract fun assignmentDao(): AssignmentDao
    abstract fun quizDao(): QuizDao
    abstract fun examDao(): ExamDao
}