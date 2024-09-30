package vn.miagi.studysmart

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowCompat
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.navigation.dependency
import dagger.hilt.android.AndroidEntryPoint
import vn.miagi.studysmart.domain.model.Session
import vn.miagi.studysmart.domain.model.Subject
import vn.miagi.studysmart.domain.model.Task
import vn.miagi.studysmart.presentation.NavGraphs
import vn.miagi.studysmart.presentation.destinations.SessionScreenRouteDestination
import vn.miagi.studysmart.presentation.session.StudySessionTimerService
import vn.miagi.studysmart.presentation.theme.StudySmartTheme

// Inject require dependencies when it created
@AndroidEntryPoint
class MainActivity : ComponentActivity()
{
    private var isBound by mutableStateOf(false)
    private lateinit var timerService: StudySessionTimerService
    private val connection = object : ServiceConnection
    {
        override fun onServiceConnected(p0: ComponentName?, service: IBinder?)
        {
            val binder =
                service as StudySessionTimerService.StudySessionTimerBinder
            timerService = binder.getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?)
        {
            isBound = false
        }

    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        // handle overlap with system's icons
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            if (isBound)
            {
                StudySmartTheme {
                    // A surface container using the 'background' color from the theme
//                TaskScreen()
                    // Navigate to the root of the app: dashboard
                    DestinationsNavHost(
                        navGraph = NavGraphs.root,
                        dependenciesContainerBuilder = {
                            dependency(SessionScreenRouteDestination) { timerService }
                        }
                    )
                }
            }
        }
        requestPermission()
    }

    override fun onStart()
    {
        super.onStart()
        Intent(this, StudySessionTimerService::class.java).also { intent ->
            bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop()
    {
        super.onStop()
        unbindService(connection)
        isBound = false
    }

    private fun requestPermission()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                0,
            )
        }
    }
}
