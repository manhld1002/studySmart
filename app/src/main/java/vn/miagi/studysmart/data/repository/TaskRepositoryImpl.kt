package vn.miagi.studysmart.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import vn.miagi.studysmart.data.local.TaskDao
import vn.miagi.studysmart.domain.model.Task
import vn.miagi.studysmart.domain.repository.TaskRepository
import vn.miagi.studysmart.tasks
//import vn.miagi.studysmart.tasks
import javax.inject.Inject

// @Inject constructor annotation to tell we will provide the object of taskDao somewhere
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
) : TaskRepository
{
    override suspend fun upsertTask(task: Task)
    {
        taskDao.upsertTask(task)
    }

    override suspend fun deleteTask(taskId: Int)
    {
        taskDao.deleteTask(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task?
    {
        return taskDao.getTaskById(taskId)
    }

    override fun getUpcomingTasksForSubject(subjectInt: Int): Flow<List<Task>>
    {
        return taskDao.getTasksForSubject(subjectInt)
            .map { tasks ->
                tasks.filter { it.isComplete.not() }
            }.map { tasks -> sortTasks(tasks) }
    }

    override fun getCompletedTasksForSubject(subjectInt: Int): Flow<List<Task>>
    {
        return taskDao.getTasksForSubject(subjectInt)
            .map { tasks -> tasks.filter { it.isComplete } }
            .map { tasks -> sortTasks(tasks) }
    }

    override fun getAllUpcomingTasks(): Flow<List<Task>>
    {
        return taskDao.getAllTask()
            .map { tasks -> tasks.filter { it.isComplete.not() } }
            .map { tasks -> sortTasks(tasks) }
    }

    private fun sortTasks(tasks: List<Task>): List<Task>
    {
        return tasks.sortedWith(compareBy<Task> { it.dueDate }.thenByDescending { it.priority })
    }
}