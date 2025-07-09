package com.meher.markbuddy.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meher.markbuddy.data.dao.*
import com.meher.markbuddy.data.entity.*
import com.meher.markbuddy.utils.GradeCalculator
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

class MarkBuddyViewModel(
    private val semesterDao: SemesterDao,
    private val courseDao: CourseDao,
    private val assignmentDao: AssignmentDao,
    private val quizDao: QuizDao,
    private val examDao: ExamDao
) : ViewModel() {

    val isLoading: StateFlow<Boolean> = MutableStateFlow(false)
    private val semesters: StateFlow<List<Semester>> = semesterDao.getAllSemesters().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    val currentSemester: StateFlow<Semester?> = semesters.map { it.maxByOrNull { it.id } }.stateIn(viewModelScope, SharingStarted.Lazily, null)
    val cgpa: StateFlow<Float?> = semesters.map { semesters ->
        semesters.filter { it.cgpa != null }.map { it.cgpa!! }.average().toFloat().takeIf { it.isFinite() }
    }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    /**
     * Returns a flow containing a list of all semesters.
     */
    fun getAllSemesters(): StateFlow<List<Semester>> = semesters

    /**
     * Returns a flow of courses for the current (most recent) semester.
     * It automatically updates when the current semester changes.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getCoursesInternal(): StateFlow<List<Course>> = // Made private
        currentSemester.flatMapLatest { semester ->
            semester?.let {
                courseDao.getCoursesForSemester(it.id)
            } ?: flowOf(emptyList())
        }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val courses: StateFlow<List<Course>> = getCoursesInternal()

    fun getCoursesForSemester(semesterId: Long): StateFlow<List<Course>> =
        courseDao.getCoursesForSemester(semesterId).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getCourse(courseId: Long): StateFlow<Course?> =
        courseDao.getCoursesForSemester(courseId).map { it.firstOrNull() }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun getSemester(semesterId: Long): StateFlow<Semester?> =
        semesters.map { it.find { it.id == semesterId } }.stateIn(viewModelScope, SharingStarted.Lazily, null)

    fun getQuizzesForCourse(courseId: Long): StateFlow<List<Quiz>> =
        quizDao.getQuizzesForCourse(courseId).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getAssignmentsForCourse(courseId: Long): StateFlow<List<Assignment>> =
        assignmentDao.getAssignmentsForCourse(courseId).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun getExamsForCourse(courseId: Long): StateFlow<List<Exam>> =
        examDao.getExamsForCourse(courseId, ExamType.entries).stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- New Function Start ---

    /**
     * Calculates the CGPA for a given list of courses.
     * This is a pure function that performs the calculation without any side effects.
     * @param courses The list of courses to be included in the CGPA calculation.
     * @return The calculated CGPA as a Float.
     */
    fun calculateCGPA(courses: List<Course>): Float {
        return GradeCalculator.calculateSemesterCgpa(courses)
    }

    // --- New Function End ---

    suspend fun addSemester(semester: Semester) {
        semesterDao.insert(semester)
    }

    suspend fun updateSemester(semester: Semester) {
        semesterDao.update(semester)
        updateSemesterCgpa(semester.id)
    }

    suspend fun deleteSemester(semester: Semester) {
        semesterDao.delete(semester)
    }

    suspend fun addCourse(course: Course, components: List<Any>? = null) {
        val courseId = courseDao.insert(course)
        components?.forEach { component ->
            when (component) {
                is Assignment -> assignmentDao.insert(component.copy(courseId = courseId))
                is Quiz -> quizDao.insert(component.copy(courseId = courseId))
                is Exam -> examDao.insert(component.copy(courseId = courseId))
            }
        }
        if (course.gpa == null) {
            updateCourseGpa(courseId)
        }
        updateSemesterCgpa(course.semesterId)
    }

    suspend fun deleteCourse(course: Course) {
        courseDao.delete(course)
        updateSemesterCgpa(course.semesterId)
    }

    /**
     * Calculates the GPA for a given course.
     * If the GPA is already stored, it returns the stored value.
     * Otherwise, it calculates the GPA, updates the course in the database, and returns the new value.
     * @param course The course for which to calculate the GPA.
     * @return The calculated or existing GPA as a Float.
     */
    suspend fun calculateGPA(course: Course): Float {
        // If GPA is already calculated, just return it.
        if (course.gpa != null) {
            return course.gpa
        }

        // Fetch all components for the course by collecting the first emission from the flows.
        val quizzes = quizDao.getQuizzesForCourse(course.id).first()
        val assignments = assignmentDao.getAssignmentsForCourse(course.id).first()
        val exams = examDao.getExamsForCourse(course.id, ExamType.entries).first()

        // Calculate GPA using the provided helper class.
        val newGpa = GradeCalculator.calculateCourseGpa(quizzes, assignments, exams, course.hasLab)

        // Update the course in the database with the newly calculated GPA.
        courseDao.update(course.copy(gpa = newGpa))

        // Return the new GPA.
        return newGpa
    }

    /**
     * Deletes a course and all of its associated components (quizzes, assignments, exams).
     * After deletion, it updates the semester's CGPA.
     * @param course The course to delete.
     */
    suspend fun deleteCourseWithComponents(course: Course) {
        val courseId = course.id
        val semesterId = course.semesterId

        // Fetch and delete all components.
        // This assumes DAOs do not have a "deleteAllByCourseId" and components must be deleted individually.
        quizDao.getQuizzesForCourse(courseId).first().forEach { quizDao.delete(it) }
        assignmentDao.getAssignmentsForCourse(courseId).first().forEach { assignmentDao.delete(it) }
        examDao.getExamsForCourse(courseId, ExamType.entries).first().forEach { examDao.delete(it) }

        // Delete the course itself.
        courseDao.delete(course)

        // Update the overall semester CGPA to reflect the removal of the course.
        updateSemesterCgpa(semesterId)
    }

    suspend fun addQuiz(quiz: Quiz) {
        quizDao.insert(quiz)
        updateCourseGpa(quiz.courseId)
        updateSemesterCgpa(getCourse(quiz.courseId).first()?.semesterId ?: 0)
    }

    suspend fun addAssignment(assignment: Assignment) {
        assignmentDao.insert(assignment)
        updateCourseGpa(assignment.courseId)
        updateSemesterCgpa(getCourse(assignment.courseId).first()?.semesterId ?: 0)
    }

    suspend fun addExam(exam: Exam) {
        examDao.insert(exam)
        updateCourseGpa(exam.courseId)
        updateSemesterCgpa(getCourse(exam.courseId).first()?.semesterId ?: 0)
    }

    private suspend fun updateCourseGpa(courseId: Long) {
        val course = getCoursesForSemester(courseId).first().find { it.id == courseId } ?: return
        val quizzes = quizDao.getQuizzesForCourse(courseId).first()
        val assignments = assignmentDao.getAssignmentsForCourse(courseId).first()
        val exams = examDao.getExamsForCourse(courseId, ExamType.entries).first()
        val gpa = GradeCalculator.calculateCourseGpa(quizzes, assignments, exams, course.hasLab)
        courseDao.update(course.copy(gpa = gpa))
    }

    private suspend fun updateSemesterCgpa(semesterId: Long) {
        val semester = semesters.first().find { it.id == semesterId } ?: return
        val courses = courseDao.getCoursesForSemester(semesterId).first()
        val cgpa = GradeCalculator.calculateSemesterCgpa(courses)
        semesterDao.update(semester.copy(cgpa = cgpa))
    }
}