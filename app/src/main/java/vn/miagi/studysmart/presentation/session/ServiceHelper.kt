package vn.miagi.studysmart.presentation.session

import android.content.Context
import android.content.Intent

object ServiceHelper
{
    fun triggerForeGroundService(context: Context, action: String) {
        Intent(context, StudySessionTimerService::class.java) .apply {
            this.action = action
            context.startService(this)
        }
    }
}