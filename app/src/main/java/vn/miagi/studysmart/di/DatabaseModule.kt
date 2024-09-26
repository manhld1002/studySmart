package vn.miagi.studysmart.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import vn.miagi.studysmart.data.local.AppDatabase
import vn.miagi.studysmart.data.local.SessionDao
import vn.miagi.studysmart.data.local.SubjectDao
import vn.miagi.studysmart.data.local.TaskDao
import javax.inject.Singleton

// mark as dagger hilt's module, define how dependencies should be provided
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule
{
    // create an object of RoomDatabase
    @Provides
    // this @Provides uses on function, provides the object of dependencies
    @Singleton
    // only one instance of object is provide throughout the application
    fun provideDatabase(application: Application): AppDatabase
    {
        return Room.databaseBuilder(
            application, AppDatabase::class.java, "studysmart.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideSubjectDao(database: AppDatabase): SubjectDao
    {
        return database.subjectDao()
    }

    @Provides
    @Singleton
    fun provideTaskDao(database: AppDatabase): TaskDao
    {
        return database.taskDao()
    }

    @Provides
    @Singleton
    fun provideSessionDao(database: AppDatabase): SessionDao
    {
        return database.sessionDao()
    }
}