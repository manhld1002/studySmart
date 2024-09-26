package vn.miagi.studysmart.presentation.subject

import androidx.compose.ui.graphics.Color
import vn.miagi.studysmart.domain.model.Session
import vn.miagi.studysmart.domain.model.Subject
import vn.miagi.studysmart.domain.model.Task

data class SubjectState(
    val currentSubjectId: Int? = null,
    val subjectName: String = "",
    val goalStudyHours: String = "",
    val studiedHours: Float = 0f,
    val subjectCardColors: List<Color>  = Subject.subjectCardColors.random(),
    val recentSessions: List<Session> = emptyList(),
    val upcomingTasks: List<Task> = emptyList(),
    val completedTasks: List<Task> = emptyList(),
    val session: Session? = null,
    val progress: Float = 0f,
    val isLoading: Boolean = false,
)
