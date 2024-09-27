package vn.miagi.studysmart.presentation.task

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.miagi.studysmart.domain.model.Task
import vn.miagi.studysmart.domain.repository.SubjectRepository
import vn.miagi.studysmart.domain.repository.TaskRepository
import vn.miagi.studysmart.presentation.navArgs
import vn.miagi.studysmart.util.Priority
import vn.miagi.studysmart.util.SnackBarEvent
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val subjectRepository: SubjectRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel()
{
    private val navArgs: TaskScreenNavArgs = savedStateHandle.navArgs()

    private val _state = MutableStateFlow(TaskState())
    val state = combine(
        _state, subjectRepository.getAllSubjects(),
    ) { state, subjects ->
        state.copy(subjects = subjects)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = TaskState()
    )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    init
    {
        fetchTask()
        fetchSubject()
    }

    fun onEvent(event: TaskEvent)
    {
        when (event)
        {
            TaskEvent.DeleteTask -> deleteTask()
            is TaskEvent.OnDateChange ->
            {
                _state.update {
                    it.copy(dueDate = event.millis)
                }
            }

            is TaskEvent.OnDescriptionChange ->
            {
                _state.update {
                    it.copy(description = event.description)
                }
            }

            TaskEvent.OnIsCompleteChange ->
            {
                _state.update {
                    it.copy(isTaskComplete = !_state.value.isTaskComplete)
                }
            }

            is TaskEvent.OnPriorityChange ->
            {
                _state.update {
                    it.copy(priority = event.priority)
                }
            }

            is TaskEvent.OnRelatedSubjectSelect ->
            {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId
                    )
                }
            }

            is TaskEvent.OnTitleChange ->
            {
                _state.update {
                    it.copy(title = event.title)
                }
            }

            TaskEvent.SaveTask -> saveTask()
        }
    }

    private fun deleteTask()
    {
        viewModelScope.launch {
            try
            {
                val currentTaskId = state.value.currentTaskId
                if (currentTaskId != null)
                {
                    // Fix error: Cannot access database on the main thread since it my potentially lock the ui
                    withContext(Dispatchers.IO) {
                        taskRepository.deleteTask(taskId = currentTaskId)
                    }
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "Task deleted successfully.")
                    )
                    _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
                } else
                {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "No task to delete.")
                    )
                }

            } catch (e: Exception)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't delete task. ${e.message}",
                        duration = SnackbarDuration.Long,
                    )
                )
            }
        }
    }

    private fun saveTask()
    {
        viewModelScope.launch {
            val state = _state.value
            if (state.subjectId == null || state.relatedToSubject == null)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Please select subject related to the task",
                        SnackbarDuration.Long
                    )
                )
                return@launch
            }
            try
            {
                taskRepository.upsertTask(
                    task = Task(
                        title = state.title,
                        description = state.description,
                        dueDate = state.dueDate ?: Instant.now().toEpochMilli(),
                        relatedToSubject = state.relatedToSubject,
                        priority = state.priority.value,
                        isComplete = state.isTaskComplete,
                        taskSubjectId = state.subjectId,
                        taskId = state.currentTaskId,
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Save task successfully"
                    )
                )
                _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
            } catch (e: Exception)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't save task. ${e.message}"
                    )
                )
            }

        }
    }

    private fun fetchTask()
    {
        viewModelScope.launch {
            navArgs.taskId?.let { id ->
                taskRepository.getTaskById(id)?.let { task ->
                    _state.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = task.dueDate,
                            isTaskComplete = task.isComplete,
                            relatedToSubject = task.relatedToSubject,
                            priority = Priority.fromInt(task.priority),
                            subjectId = task.taskSubjectId,
                            currentTaskId = task.taskId,
                        )
                    }
                }
            }
        }
    }

    private fun fetchSubject()
    {
        viewModelScope.launch {
            navArgs.subjectId?.let { id ->
                subjectRepository.getSubjectById(id)?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectId = subject.subjectId,
                            relatedToSubject = subject.name,
                        )
                    }
                }
            }
        }
    }
}