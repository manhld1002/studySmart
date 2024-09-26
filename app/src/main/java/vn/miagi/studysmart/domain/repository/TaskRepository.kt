package vn.miagi.studysmart.domain.repository

import kotlinx.coroutines.flow.Flow
import vn.miagi.studysmart.domain.model.Task

interface TaskRepository
{
    suspend fun upsertTask(task: Task)

    suspend fun deleteTask(taskId: Int)

    suspend fun getTaskById(taskId: Int) : Task?

    fun getUpcomingTasksForSubject(subjectInt: Int) : Flow<List<Task>>

    fun getCompletedTasksForSubject(subjectInt: Int) : Flow<List<Task>>

    fun getAllUpcomingTasks() : Flow<List<Task>>
}