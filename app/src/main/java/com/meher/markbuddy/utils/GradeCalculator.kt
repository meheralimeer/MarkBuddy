package com.meher.markbuddy.utils

import com.meher.markbuddy.data.entity.*

object GradeCalculator {
    private const val THEORY_QUIZ_WEIGHT = 0.15f
    private const val THEORY_ASSIGNMENT_WEIGHT = 0.10f
    private const val THEORY_MIDTERM_WEIGHT = 0.25f
    private const val THEORY_TERMINAL_WEIGHT = 0.50f

    private const val LAB_ASSIGNMENT_WEIGHT = 0.25f
    private const val LAB_MIDTERM_WEIGHT = 0.25f
    private const val LAB_TERMINAL_WEIGHT = 0.50f

    private const val THEORY_PORTION_WEIGHT = 0.75f
    private const val LAB_PORTION_WEIGHT = 0.25f

    private fun marksToGpa(marks: Float): Float = when {
        marks >= 85 -> 4.0f
        marks >= 80 -> 3.7f
        marks >= 75 -> 3.3f
        marks >= 70 -> 3.0f
        marks >= 65 -> 2.7f
        marks >= 60 -> 2.3f
        marks >= 55 -> 2.0f
        marks >= 50 -> 1.7f
        else -> 0.0f
    }

    fun calculateCourseGpa(
        quizzes: List<Quiz>,
        assignments: List<Assignment>,
        exams: List<Exam>,
        hasLab: Boolean
    ): Float {
        val quizCount = quizzes.size
        val quizWeightPerItem = if (quizCount > 0) THEORY_QUIZ_WEIGHT / quizCount else 0f
        val quizMarks = quizzes.sumOf { (it.obtainedMarks / it.totalMarks * quizWeightPerItem).toDouble() }.toFloat()

        val theoryAssignments = assignments.filter { !it.isLab }
        val assignmentCount = theoryAssignments.size
        val assignmentWeightPerItem = if (assignmentCount > 0) THEORY_ASSIGNMENT_WEIGHT / assignmentCount else 0f
        val assignmentMarks = theoryAssignments.sumOf { (it.obtainedMarks / it.totalMarks * assignmentWeightPerItem).toDouble() }.toFloat()

        val midtermExam = exams.find { it.type == ExamType.MIDTERM }
        val midtermMarks = midtermExam?.let { it.obtainedMarks / it.totalMarks * THEORY_MIDTERM_WEIGHT } ?: 0f

        val terminalExam = exams.find { it.type == ExamType.TERMINAL }
        val terminalMarks = terminalExam?.let { it.obtainedMarks / it.totalMarks * THEORY_TERMINAL_WEIGHT } ?: 0f

        val theoryTotal = (quizMarks + assignmentMarks + midtermMarks + terminalMarks) * 100

        if (!hasLab) {
            return marksToGpa(theoryTotal)
        }

        val labAssignments = assignments.filter { it.isLab }
        val labAssignmentCount = labAssignments.size
        val labAssignmentWeightPerItem = if (labAssignmentCount > 0) LAB_ASSIGNMENT_WEIGHT / labAssignmentCount else 0f
        val labAssignmentMarks = labAssignments.sumOf { (it.obtainedMarks / it.totalMarks * labAssignmentWeightPerItem).toDouble() }.toFloat()

        val labMidtermExam = exams.find { it.type == ExamType.LAB_MIDTERM }
        val labMidtermMarks = labMidtermExam?.let { it.obtainedMarks / it.totalMarks * LAB_MIDTERM_WEIGHT } ?: 0f

        val labTerminalExam = exams.find { it.type == ExamType.LAB_TERMINAL }
        val labTerminalMarks = labTerminalExam?.let { it.obtainedMarks / it.totalMarks * LAB_TERMINAL_WEIGHT } ?: 0f

        val labTotal = (labAssignmentMarks + labMidtermMarks + labTerminalMarks) * 100

        val finalMarks = (theoryTotal * THEORY_PORTION_WEIGHT) + (labTotal * LAB_PORTION_WEIGHT)
        return marksToGpa(finalMarks)
    }

    fun calculateSemesterCgpa(courses: List<Course>): Float {
        if (courses.isEmpty()) return 0.0f
        val totalCreditHours = courses.sumOf { it.creditHours.toDouble() }.toFloat()
        val weightedGpaSum = courses.sumOf { (it.gpa ?: 0f) * it.creditHours.toDouble() }.toFloat()
        return if (totalCreditHours > 0) weightedGpaSum / totalCreditHours else 0.0f
    }
}