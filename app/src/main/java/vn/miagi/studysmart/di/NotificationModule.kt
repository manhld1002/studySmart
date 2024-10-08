package vn.miagi.studysmart.di

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ServiceComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ServiceScoped
import vn.miagi.studysmart.R
import vn.miagi.studysmart.presentation.session.ServiceHelper
import vn.miagi.studysmart.util.Constants.NOTIFICATION_CHANNEL_ID

@Module
@InstallIn(ServiceComponent::class)
object NotificationModule
{
    @ServiceScoped
    @Provides
    fun provideNotificationBuilder(
        @ApplicationContext context: Context
    ): NotificationCompat.Builder
    {
        return NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
            .setContentTitle("Study session")
            .setContentText("00:00:00")
            .setSmallIcon(
                R.drawable.ic_launcher_foreground
            )
            .setOngoing(true)
            .setContentIntent(ServiceHelper.clickPendingIntent(context))
    }

    // Create notification manager object
    @ServiceScoped
    @Provides
    fun provideNotificationManager(
        @ApplicationContext context: Context
    ): NotificationManager
    {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }
}