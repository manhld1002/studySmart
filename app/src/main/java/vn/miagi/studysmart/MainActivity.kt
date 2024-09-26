package vn.miagi.studysmart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import com.ramcosta.composedestinations.DestinationsNavHost
import dagger.hilt.android.AndroidEntryPoint
import vn.miagi.studysmart.domain.model.Session
import vn.miagi.studysmart.domain.model.Subject
import vn.miagi.studysmart.domain.model.Task
import vn.miagi.studysmart.presentation.NavGraphs
import vn.miagi.studysmart.presentation.theme.StudySmartTheme

// Inject require dependencies when it created
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // handle overlap with system's icons
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            StudySmartTheme {
                // A surface container using the 'background' color from the theme
//                TaskScreen()
                // Navigate to the root of the app: dashboard
                DestinationsNavHost(navGraph = NavGraphs.root)
            }

        }
    }
}

val subjects = listOf(
    Subject(
        name = "English",
        goalHours = 10f,
        colors = Subject.subjectCardColors[0].map { it.toArgb() },
        subjectId = 0
    ), Subject(
        name = "Physics",
        goalHours = 10f,
        colors = Subject.subjectCardColors[1].map { it.toArgb() },
        subjectId = 0
    ), Subject(
        name = "Maths",
        goalHours = 10f,
        colors = Subject.subjectCardColors[2].map { it.toArgb() },
        subjectId = 0
    ), Subject(
        name = "Geology",
        goalHours = 10f,
        colors = Subject.subjectCardColors[3].map { it.toArgb() },
        subjectId = 0
    ), Subject(
        name = "Fine Arts",
        goalHours = 10f,
        colors = Subject.subjectCardColors[4].map { it.toArgb() },
        subjectId = 0
    )
)
val tasks = listOf(
    Task(
        title = "Prepare notes",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Do Homework",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Go Coaching",
        description = "",
        dueDate = 0L,
        priority = 2,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Assignment",
        description = "",
        dueDate = 0L,
        priority = 1,
        relatedToSubject = "",
        isComplete = false,
        taskSubjectId = 0,
        taskId = 1
    ),
    Task(
        title = "Write Poem",
        description = "",
        dueDate = 0L,
        priority = 0,
        relatedToSubject = "",
        isComplete = true,
        taskSubjectId = 0,
        taskId = 1
    ),

    )
val sessions = listOf(
    Session(
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0,
    ),
    Session(
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0,
    ),
    Session(
        relatedToSubject = "Physics",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0,
    ),
    Session(
        relatedToSubject = "Maths",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0,
    ),
    Session(
        relatedToSubject = "English",
        date = 0L,
        duration = 2,
        sessionSubjectId = 0,
        sessionId = 0,
    ),
)

