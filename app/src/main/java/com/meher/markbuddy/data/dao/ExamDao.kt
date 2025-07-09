package com.meher.markbuddy.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.meher.markbuddy.data.entity.Exam
import com.meher.markbuddy.data.entity.ExamType
import kotlinx.coroutines.flow.Flow

@Dao
interface ExamDao {
    @Query("SELECT * FROM exams WHERE courseId = :courseId AND type IN (:types)")
    fun getExamsForCourse(courseId: Long, types: List<ExamType>): Flow<List<Exam>>

    @Insert
    suspend fun insert(exam: Exam): Long

    @Update
    suspend fun update(exam: Exam)

    @Delete
    suspend fun delete(exam: Exam)
}