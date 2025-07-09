package com.meher.markbuddy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meher.markbuddy.data.entity.Quiz
import kotlinx.coroutines.flow.Flow

@Dao
interface QuizDao {
    @Query("SELECT * FROM quizzes WHERE courseId = :courseId")
    fun getQuizzesForCourse(courseId: Long): Flow<List<Quiz>>

    @Insert
    suspend fun insert(quiz: Quiz): Long

    @Update
    suspend fun update(quiz: Quiz)

    @Delete
    suspend fun delete(quiz: Quiz)
}