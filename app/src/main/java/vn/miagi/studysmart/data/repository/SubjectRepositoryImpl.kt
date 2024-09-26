package vn.miagi.studysmart.data.repository

import kotlinx.coroutines.flow.Flow
import vn.miagi.studysmart.data.local.SubjectDao
import vn.miagi.studysmart.domain.model.Subject
import vn.miagi.studysmart.domain.repository.SubjectRepository
import javax.inject.Inject

class SubjectRepositoryImpl @Inject constructor(
    private val subjectDao: SubjectDao
) : SubjectRepository
{
    override suspend fun upsertSubject(subject: Subject)
    {
        subjectDao.upsertSubject(subject)
    }

    override fun getTotalSubjectCount(): Flow<Int>
    {
        return subjectDao.getTotalSubjectCount()
    }

    override fun getTotalGoalHours(): Flow<Float>
    {
        return subjectDao.getTotalGoalHours()
    }

    override suspend fun deleteSubject(subjectInt: Int)
    {
        TODO("Not yet implemented")
    }

    override suspend fun getSubjectById(subjectId: Int): Subject?
    {
        TODO("Not yet implemented")
    }

    override fun getAllSubjects(): Flow<List<Subject>>
    {
        return subjectDao.getAllSubjects()
    }
}