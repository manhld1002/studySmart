package vn.miagi.studysmart.domain.repository

import kotlinx.coroutines.flow.Flow
import vn.miagi.studysmart.domain.model.Subject

interface SubjectRepository
{
    suspend fun upsertSubject(subject: Subject)

    fun getTotalSubjectCount(): Flow<Int>

    fun getTotalGoalHours(): Flow<Float>

    suspend fun deleteSubject(subjectInt: Int)

    suspend fun getSubjectById(subjectId: Int): Subject?

    fun getAllSubjects(): Flow<List<Subject>>
}