package vn.miagi.studysmart.presentation.session

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import vn.miagi.studysmart.util.Constants.ACTION_SERVICE_CANCEL
import vn.miagi.studysmart.util.Constants.ACTION_SERVICE_START
import vn.miagi.studysmart.util.Constants.ACTION_SERVICE_STOP
import vn.miagi.studysmart.util.Constants.NOTIFICATION_CHANNEL_ID
import vn.miagi.studysmart.util.Constants.NOTIFICATION_CHANNEL_NAME
import vn.miagi.studysmart.util.Constants.NOTIFICATION_ID
import javax.inject.Inject

@AndroidEntryPoint
class StudySessionTimerService : Service()
{
    @Inject
    lateinit var notificationManager: NotificationManager

    @Inject
    lateinit var notificationBuilder: NotificationCompat.Builder
    // bind services to component
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        intent?.action.let {
            when (it)
            {
                ACTION_SERVICE_START ->
                {
                    startForegroundService()
                }

                ACTION_SERVICE_STOP ->
                {
                }

                ACTION_SERVICE_CANCEL ->
                {
                }
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    private fun startForegroundService()
    {
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun createNotificationChannel()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            val channel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}