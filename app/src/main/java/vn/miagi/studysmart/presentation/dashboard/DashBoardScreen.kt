package vn.miagi.studysmart.presentation.dashboard

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import vn.miagi.studysmart.R
import vn.miagi.studysmart.domain.model.Session
import vn.miagi.studysmart.domain.model.Subject
import vn.miagi.studysmart.domain.model.Task
import vn.miagi.studysmart.presentation.components.AddSubjectDialog
import vn.miagi.studysmart.presentation.components.CountCard
import vn.miagi.studysmart.presentation.components.DeleteDialog
import vn.miagi.studysmart.presentation.components.StudySessionsList
import vn.miagi.studysmart.presentation.components.SubjectCard
import vn.miagi.studysmart.presentation.components.TasksList
import vn.miagi.studysmart.presentation.destinations.SessionScreenRouteDestination
import vn.miagi.studysmart.presentation.destinations.SubjectScreenRouteDestination
import vn.miagi.studysmart.presentation.destinations.TaskScreenRouteDestination
import vn.miagi.studysmart.presentation.subject.SubjectScreenNavArgs
import vn.miagi.studysmart.presentation.task.TaskScreenNavArgs
import vn.miagi.studysmart.util.SnackBarEvent

@Destination(start = true)
@Composable
fun DashboardScreenRoute(
    navigator: DestinationsNavigator
)
{

    val viewModel: DashboardViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val tasks by viewModel.tasks.collectAsStateWithLifecycle()
    val recentSessions by viewModel.recentSession.collectAsStateWithLifecycle()

    DashboardScreen(
        state = state,
        tasks = tasks,
        recentSession = recentSessions,
        onEvent = viewModel::onEvent,
        snackBarEvent = viewModel.snackBarEventFlow,
        onSubjectCardClick = { subjectId ->
            subjectId?.let {
                val navArg = SubjectScreenNavArgs(subjectId = subjectId)
                navigator.navigate(SubjectScreenRouteDestination(navArgs = navArg))
            }

        },
        onTaskCardClick = { taskId ->
            taskId?.let {
                val navArg =
                    TaskScreenNavArgs(taskId = taskId, subjectId = null)
                navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
            }
        },
        onStartSessionButtonClick = {
            navigator.navigate(SessionScreenRouteDestination())
        },
    )
}

@Composable
private fun DashboardScreen(
    state: DashboardState,
    tasks: List<Task>,
    recentSession: List<Session>,
    onEvent: (DashboardEvent) -> Unit,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onSubjectCardClick: (Int?) -> Unit,
    onTaskCardClick: (Int?) -> Unit,
    onStartSessionButtonClick: () -> Unit,
)
{

    // save even screen navigation and dark/light mode : rememberSaveable
    var isAddSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteSessionDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    val snackBarHostState = remember {
        SnackbarHostState()
    }

    LaunchedEffect(key1 = true) {
        snackBarEvent.collectLatest { event ->
            when (event)
            {
                is SnackBarEvent.ShowSnackBar ->
                {
                    snackBarHostState.showSnackbar(
                        message = event.message,
                        duration = event.duration,
                    )
                }
            }
        }
    }


    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = { onEvent(DashboardEvent.OnSubjectNameChange(it)) },
        onGoalHoursChange = { onEvent(DashboardEvent.OnGoalStudyHoursChange(it)) },
        selectedColors = state.subjectCardColors,
        onColorChange = { onEvent(DashboardEvent.OnSubjectCardColorChange(it)) },
        onDismissRequest = {
            isAddSubjectDialogOpen = false
        },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.SaveSubject)
            isAddSubjectDialogOpen = false
        },
    )

    DeleteDialog(isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session?",
        bodyText = "Do you want to delete this session? Your studied hours will be reduced " + "by this session time. This action can not be undo",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(DashboardEvent.DeleteSession)
            isDeleteSessionDialogOpen = false
        })

    Scaffold(
        topBar = {
            DashboardScreenTopBar()
        },
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Counts card Section
            item {
                CountCardsSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    subjectCount = state.totalSubjectCount,
                    studiedHours = state.totalStudiedHours.toString(),
                    goalHours = state.totalGoalStudyHours.toString(),
                )
            }
            // SubjectCard Section
            item {
                SubjectCardsSelection(
                    modifier = Modifier.fillMaxWidth(),
                    // create an emptyList()
                    subjectList = state.subjects,
                    onAddIconClicked = {
                        isAddSubjectDialogOpen = true
                    },
                    onSubjectCardClick = onSubjectCardClick,
                )
            }

            // Button
            item {
                Button(
                    onClick = onStartSessionButtonClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 20.dp)
                ) {
                    Text(text = "Start study session")
                }
            }

            item {
                TasksList(
                    sectionTitle = "UPCOMING TASKS",
                    emptyListText = "You don't have any upcoming tasks. \n Click the + button to add new task",
                    tasksList = tasks,
                    onCheckBoxClick = {
                        onEvent(
                            DashboardEvent.OnTaskIsCompleteChange(
                                it
                            )
                        )
                    },
                    onTaskCardClick = onTaskCardClick,
                )
                Spacer(modifier = Modifier.height(20.dp))
                StudySessionsList(
                    sectionTitle = "RECENT STUDY SESSIONS",
                    emptyListText = "You don't have any recent study sessions. \n Start a study session to begin recording your progress",
                    sessions = recentSession,
                    onDeleteIconClick = {
                        onEvent(DashboardEvent.OnDeleteSessionButtonClick(it))
                        isDeleteSessionDialogOpen = true
                    },
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DashboardScreenTopBar()
{
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "StudySmart",
                style = MaterialTheme.typography.headlineMedium
            )
        },
    )
}

@Composable
private fun CountCardsSection(
    modifier: Modifier,
    subjectCount: Int,
    studiedHours: String,
    goalHours: String,
)
{
    Row(
        modifier = modifier
    ) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Subject Count",
            count = "$subjectCount",
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours,
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours,
        )
    }
}

@Composable
private fun SubjectCardsSelection(
    modifier: Modifier,
    subjectList: List<Subject>,
    emptyListText: String = "You don't have any subjects. \n Click the + button to add new subject",
    onAddIconClicked: () -> Unit,
    onSubjectCardClick: (Int?) -> Unit
)
{
    Column(
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = "SUBJECTS",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp)
            )
            IconButton(onClick = onAddIconClicked) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject"
                )
            }
        }
        if (subjectList.isEmpty())
        {
            Image(
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally),
                painter = painterResource(id = R.drawable.img_books),
                contentDescription = emptyListText,
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = emptyListText,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center,
            )
        }
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(start = 12.dp, end = 12.dp)
        ) {
            items(subjectList) { subject ->
                SubjectCard(
                    subjectName = subject.name,
                    gradientColors = subject.colors.map { Color(it) },
                    onClick = {
                        onSubjectCardClick(subject.subjectId)
                    },
                )
            }
        }
    }
}