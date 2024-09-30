package vn.miagi.studysmart.presentation.session

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import vn.miagi.studysmart.domain.model.Session
import vn.miagi.studysmart.domain.repository.SessionRepository
import vn.miagi.studysmart.domain.repository.SubjectRepository
import vn.miagi.studysmart.util.SnackBarEvent
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class SessionViewModel @Inject constructor(
    subjectRepository: SubjectRepository,
    private val sessionRepository: SessionRepository
) : ViewModel()
{
    private val _state = MutableStateFlow(SessionState())
    val state = combine(
        _state,
        subjectRepository.getAllSubjects(),
        sessionRepository.getAllSessions(),
    ) { state, subjects, sessions ->
        state.copy(
            subjects = subjects,
            sessions = sessions,
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5000),
        initialValue = SessionState()
    )

    // Implement for snackbar event
    private val _snackBarEventFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEventFlow = _snackBarEventFlow.asSharedFlow()

    fun onEvent(event: SessionEvent)
    {
        when (event)
        {
            SessionEvent.NotifyToUpdateSubject ->
            {
                notifyToUpdateSubject()
            }

            SessionEvent.DeleteSession ->
            {
                deleteSession()
            }

            is SessionEvent.OnDeleteSessionButtonClick ->
            {
                _state.update {
                    it.copy(session = event.session)
                }
            }

            is SessionEvent.OnRelatedSubjectChange ->
            {
                _state.update {
                    it.copy(
                        relatedToSubject = event.subject.name,
                        subjectId = event.subject.subjectId,
                    )
                }
            }

            is SessionEvent.SaveSession ->
            {
                insertSession(event.duration)
            }

            is SessionEvent.UpdateSubjectIdAndRelatedSubject ->
            {
                _state.update {
                    it.copy(
                        relatedToSubject = event.relatedToSubject,
                        subjectId = event.subjectId,
                    )
                }
            }
        }
    }

    private fun notifyToUpdateSubject()
    {
        viewModelScope.launch {
            if(state.value.subjectId == null || state.value.relatedToSubject == null) {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Please select subject related to the session"
                    )
                )
            }
        }
    }

    private fun deleteSession()
    {
        viewModelScope.launch {
            try
            {
                state.value.session?.let {
                    sessionRepository.deleteSession(it)
                }
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(message = "Session deleted successfully")
                )
            } catch (e: Exception)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't delete session. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }

        }
    }

    private fun insertSession(duration: Long)
    {
        viewModelScope.launch {
            if (duration < 36)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Single session can not be less than 36 seconds"
                    )
                )
                return@launch
            }
            try
            {
                sessionRepository.insertSession(
                    session = Session(
                        sessionSubjectId = state.value.subjectId ?: -1,
                        relatedToSubject = state.value.relatedToSubject
                            ?: "",
                        date = Instant.now().toEpochMilli(),
                        duration = duration
                    )
                )
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Session saved successfully",
                    )
                )
            } catch (e: Exception)
            {
                _snackBarEventFlow.emit(
                    SnackBarEvent.ShowSnackBar(
                        message = "Couldn't update Session. ${e.message}",
                        duration = SnackbarDuration.Long
                    )
                )
            }

        }
    }
}