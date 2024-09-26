package vn.miagi.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.miagi.studysmart.domain.repository.SessionRepository
import vn.miagi.studysmart.domain.repository.SubjectRepository
import vn.miagi.studysmart.domain.repository.TaskRepository
import vn.miagi.studysmart.presentation.navArgs
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
    ) {state, upcomingTasks, completedTask, recentSessions, totalSessionsDuration ->
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

    init
    {
        fetchSubject()
    }

    fun onEvent(event: SubjectEvent) {
        when(event) {
            SubjectEvent.DeleteSession -> TODO()
            SubjectEvent.DeleteSubject -> TODO()
            is SubjectEvent.OnDeleteSessionButtonClick -> TODO()
            is SubjectEvent.OnGoalStudyHoursChange -> TODO()
            is SubjectEvent.OnSubjectCardColorChange -> TODO()
            is SubjectEvent.OnSubjectNameChange -> TODO()
            is SubjectEvent.OnTaskIsCompleteChange -> TODO()
            SubjectEvent.UpdateSubject -> TODO()
        }
    }

    private fun fetchSubject() {
        viewModelScope.launch {
            subjectRepository.getSubjectById(navArgs.subjectId)?.let {
                subject->
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
}