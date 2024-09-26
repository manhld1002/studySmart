package vn.miagi.studysmart.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vn.miagi.studysmart.data.repository.SessionRepositoryImpl
import vn.miagi.studysmart.data.repository.SubjectRepositoryImpl
import vn.miagi.studysmart.data.repository.TaskRepositoryImpl
import vn.miagi.studysmart.domain.repository.SessionRepository
import vn.miagi.studysmart.domain.repository.SubjectRepository
import vn.miagi.studysmart.domain.repository.TaskRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule
{
    @Singleton
    // Binding an interface to implementation
    @Binds
    abstract fun bindSubjectRepository(impl: SubjectRepositoryImpl) : SubjectRepository

    @Singleton
    // Binding an interface to implementation
    @Binds
    abstract fun bindTaskRepository(impl: TaskRepositoryImpl) : TaskRepository

    @Singleton
    // Binding an interface to implementation
    @Binds
    abstract fun bindSessionRepository(impl: SessionRepositoryImpl) : SessionRepository
}