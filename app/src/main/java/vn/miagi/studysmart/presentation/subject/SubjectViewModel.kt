package vn.miagi.studysmart.presentation.subject

import androidx.compose.material3.SnackbarDuration
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import vn.miagi.studysmart.domain.model.Subject
import vn.miagi.studysmart.domain.model.Task
import vn.miagi.studysmart.domain.repository.SessionRepository
import vn.miagi.studysmart.domain.repository.SubjectRepository
import vn.miagi.studysmart.domain.repository.TaskRepository
import vn.miagi.studysmart.presentation.navArgs
import vn.miagi.studysmart.util.SnackBarEvent
import vn.miagi.studysmart.util.toHours
import javax.inject.Inject

@HiltViewModel
class SubjectViewModel @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val taskRepository: TaskRepository,
    private val sessionRepository: SessionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel()
{

    private val navArgs: SubjectScreenNavArgs = savedStateHandle.navArgs()

    private val _state = MutableStateFlow(SubjectState())
    val state = combine(
        _state,
        taskRepository.getUpcomingTasksForSubject(navArgs.subjectId),
        taskRepository.getCompletedTasksForSubject(navArgs.subjectId),
        sessionRepository.getRecentTenSessionsForSubject(navArgs.subjectId),
        sessionRepository.getTotalSessionsDurationBySubject(navArgs.subjectId)
    ) { state, upcomingTasks, completedTask, recentSessions, totalSessionsDuration ->
        state.copy(
            upcomingTasks = upcomingTasks,
            completedTasks = completedTask,
            recentSessions = recentSessions,
            studiedHours = totalSessionsDuration.toHours()
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SubjectState()
    )

    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    init
    {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvent)
    {
        when (event)
        {
            SubjectEvent.DeleteSession ->
            {

            }

            SubjectEvent.DeleteSubject -> deleteSubject()
            is SubjectEvent.OnDeleteSessionButtonClick -> TODO()
            is SubjectEvent.OnGoalStudyHoursChange ->
            {
                _state.update {
                    it.copy(goalStudyHours = event.hours)
                }
            }

            is SubjectEvent.OnSubjectCardColorChange ->
            {
                _state.update {
                    it.copy(subjectCardColors = event.color)
                }
            }

            is SubjectEvent.OnSubjectNameChange ->
            {
                _state.update {
                    it.copy(subjectName = event.name)
                }
            }

            is SubjectEvent.OnTaskIsCompleteChange ->
            {
                updateTask(event.task)
            }

            SubjectEvent.UpdateSubject -> updateSubject()
            SubjectEvent.UpdateProgress ->
            {
                val goalStudyHours =
                    state.value.goalStudyHours.toFloatOrNull() ?: 1f
                _state.update {
                    it.copy(
                        progress = (state.value.studiedHours / goalStudyHours).coerceIn(
                            0f, 1f
                        )
                    )
                }
            }
        }
    }

    private fun fetchSubject()
    {
        viewModelScope.launch {
            subjectRepository.getSubjectById(navArgs.subjectId)
                ?.let { subject ->
                    _state.update {
                        it.copy(
                            subjectName = subject.name,
                            goalStudyHours = subject.goalHours.toString(),
                            subjectCardColors = subject.colors.map { Color(it) },
                            currentSubjectId = subject.subjectId,
                        )
                    }
                }
        }
    }

    private fun updateSubject()
    {
        // like setState() auto clear when its done
        viewModelScope.launch {
            try
            {
                subjectRepository.upsertSubject(
                    subject = Subject(subjectId = state.value.currentSubjectId,
                        name = state.value.subjectName,
                        goalHours = state.value.goalStudyHours.toFloatOrNull()
                            ?: 1f,
                        colors = state.value.subjectCardColors.map { it.toArgb() })
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(message = "Subject updated successfully. ")
                )
            } catch (e: Exception)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't update subject. ${e.message}",
                        SnackbarDuration.Long,
                    )
                )
            }

        }
    }

    private fun deleteSubject()
    {
        viewModelScope.launch {
            try
            {

                val currentSubjectId = state.value.currentSubjectId
                if (currentSubjectId != null)
                {
                    // Fix error: Cannot access database on the main thread since it my potentially lock the ui
                    withContext(Dispatchers.IO) {
                        subjectRepository.deleteSubject(subjectInt = currentSubjectId)
                    }
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "Subject deleted successfully.")
                    )
                    _snackBarEventFlow.emit(SnackBarEvent.NavigateUp)
                } else
                {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(message = "No subject to delete.")
                    )
                }

            } catch (e: Exception)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't delete subject. ${e.message}",
                        duration = SnackbarDuration.Long,
                    )
                )
            }
        }
    }

    private fun updateTask(task: Task)
    {
        viewModelScope.launch {
            try
            {
                taskRepository.upsertTask(task = task.copy(isComplete = !task.isComplete))
                if(task.isComplete) {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            message = "Saved in upcoming tasks."
                        )
                    )
                } else {
                    _snackBarEventFlow.emit(
                        SnackBarEvent.ShowSnackBar(
                            message = "Saved in completed tasks."
                        )
                    )
                }

            } catch (e: Exception)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't update task. ${e.message}",
                        SnackbarDuration.Long
                    )
                )
            }

        }
    }
}