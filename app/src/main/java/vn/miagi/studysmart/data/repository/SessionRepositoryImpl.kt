package vn.miagi.studysmart.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.take
import vn.miagi.studysmart.data.local.SessionDao
import vn.miagi.studysmart.domain.model.Session
import vn.miagi.studysmart.domain.repository.SessionRepository
import javax.inject.Inject

class SessionRepositoryImpl @Inject constructor(
    private val sessionDao: SessionDao
) : SessionRepository
{
    override suspend fun insertSession(session: Session)
    {
        sessionDao.insertSession(session)
    }

    override suspend fun deleteSession(session: Session)
    {
        TODO("Not yet implemented")
    }

    override fun getAllSessions(): Flow<List<Session>>
    {
        TODO("Not yet implemented")
    }

    override fun getRecentFiveSessions(): Flow<List<Session>>
    {
        return sessionDao.getAllSessions().take(5)
    }

    override fun getRecentTenSessionsForSubject(subjectId: Int): Flow<List<Session>>
    {
        TODO("Not yet implemented")
    }

    override fun getTotalSessionsDuration(): Flow<Long>
    {
        return sessionDao.getTotalSessionDuration()
    }

    override fun getTotalSessionsDurationBySubjectId(subjectId: Int): Flow<Long>
    {
        TODO("Not yet implemented")
    }
}