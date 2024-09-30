package vn.miagi.studysmart.presentation.session

import vn.miagi.studysmart.domain.model.Session
import vn.miagi.studysmart.domain.model.Subject

data class SessionState(
    val subjects: List<Subject> = emptyList(),
    val sessions: List<Session> = emptyList(),
    val relatedToSubject: String? = null,
    val subjectId: Int? = null,
    val session: Session? = null
)
