package vn.miagi.studysmart.presentation.session

import android.app.Service
import android.content.Intent
import android.os.IBinder
import vn.miagi.studysmart.util.Constants.ACTION_SERVICE_CANCEL
import vn.miagi.studysmart.util.Constants.ACTION_SERVICE_START
import vn.miagi.studysmart.util.Constants.ACTION_SERVICE_STOP

class StudySessionTimerService : Service()
{
    // bind services to component
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int
    {
        intent?.action.let {
            when (it)
            {
                ACTION_SERVICE_START ->
                {
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
}