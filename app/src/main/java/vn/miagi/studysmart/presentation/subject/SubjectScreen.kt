package vn.miagi.studysmart.presentation.subject

import  androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import vn.miagi.studysmart.presentation.components.AddSubjectDialog
import vn.miagi.studysmart.presentation.components.CountCard
import vn.miagi.studysmart.presentation.components.DeleteDialog
import vn.miagi.studysmart.presentation.components.StudySessionsList
import vn.miagi.studysmart.presentation.components.TasksList
import vn.miagi.studysmart.presentation.destinations.TaskScreenRouteDestination
import vn.miagi.studysmart.presentation.task.TaskScreenNavArgs
import vn.miagi.studysmart.util.SnackBarEvent

data class SubjectScreenNavArgs(
    val subjectId: Int
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(
    navigator: DestinationsNavigator
)
{
    val viewModel: SubjectViewModel = hiltViewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    SubjectScreen(
        state = state,
        onEvent = viewModel::onEvent,
        snackBarEvent = viewModel.snackBarEventFlow,
        onBackButtonClick = { navigator.navigateUp() },
        onAddTaskButtonClick = {
            val navArg = TaskScreenNavArgs(taskId = null, subjectId = state.currentSubjectId)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
        onTaskCardClick = { taskId ->
            val navArg = TaskScreenNavArgs(taskId = taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(
    state: SubjectState,
    onEvent: (SubjectEvent) -> Unit,
    snackBarEvent: SharedFlow<SnackBarEvent>,
    onBackButtonClick: () -> Unit,
    onAddTaskButtonClick: () -> Unit,
    onTaskCardClick: (Int?) -> Unit,
)
{
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val listState = rememberLazyListState()
    // for continuously change and capture the latest change (more than update the UI)
    val isFABExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }
    // save even screen navigation and dark/light mode : rememberSaveable
    var isAddSubjectDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }
    var isDeleteSessionDialogOpen by rememberSaveable {
        mutableStateOf(false)
    }

    var isDeleteSubjectDialogOpen by rememberSaveable {
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

                SnackBarEvent.NavigateUp ->
                {
                    onBackButtonClick()
                }
            }
        }
    }

    // Whenever change in these 2 keys, call LaunchedEffect()
    LaunchedEffect(key1 = state.studiedHours, key2 = state.goalStudyHours) {
        onEvent(SubjectEvent.UpdateProgress)
    }


    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        subjectName = state.subjectName,
        goalHours = state.goalStudyHours,
        onSubjectNameChange = { onEvent(SubjectEvent.OnSubjectNameChange(it)) },
        onGoalHoursChange = { onEvent(SubjectEvent.OnGoalStudyHoursChange(it)) },
        selectedColors = state.subjectCardColors,
        onColorChange = { onEvent(SubjectEvent.OnSubjectCardColorChange(it)) },
        onDismissRequest = {
            isAddSubjectDialogOpen = false
        },
        onConfirmButtonClick = {
            onEvent(SubjectEvent.UpdateSubject)
            isAddSubjectDialogOpen = false
        },
    )

    DeleteDialog(isOpen = isDeleteSubjectDialogOpen,
        title = "Delete Subject?",
        bodyText = "Do you want to delete this subject? All related tasks and study sessions will be permanently removed. This action can not be undone.",
        onDismissRequest = { isDeleteSubjectDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSubject)
            isDeleteSubjectDialogOpen = false
        })

    DeleteDialog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Delete Session?",
        bodyText = "Do you want to delete this session? Your studied hours will be reduced by this session time. This action can not be undone.",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = {
            onEvent(SubjectEvent.DeleteSession)
            isDeleteSessionDialogOpen = false
        },
    )

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState)
        },
        // notify scroll behavior for the scaffold
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                title = state.subjectName,
                onBackButtonClick = onBackButtonClick,
                onDeleteButtonClick = {
                    isDeleteSubjectDialogOpen = true
                },
                onEditButtonClick = {
                    isAddSubjectDialogOpen = true
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(onClick = onAddTaskButtonClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                },
                text = { Text(text = "Add Task") },
                expanded = isFABExpanded
            )
        },
    ) { paddingValues ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
        ) {
            item {
                SubjectOverviewSection(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    studiedHours = state.studiedHours.toString(),
                    goalHours = state.goalStudyHours,
                    progress = state.progress,
                )
            }
            item {
                TasksList(
                    sectionTitle = "UPCOMING TASKS",
                    emptyListText = "You don't have any upcoming tasks. \n Click the + button to add new task",
                    tasksList = state.upcomingTasks,
                    onCheckBoxClick = {
                        onEvent(
                            SubjectEvent.OnTaskIsCompleteChange(
                                it
                            )
                        )
                    },
                    onTaskCardClick = onTaskCardClick,
                )
                Spacer(modifier = Modifier.height(20.dp))
                TasksList(
                    sectionTitle = "COMPLETED TASKS",
                    emptyListText = "You don't have any completed tasks. \n Click the check box on completion of task",
                    tasksList = state.completedTasks,
                    onCheckBoxClick = {
                        onEvent(
                            SubjectEvent.OnTaskIsCompleteChange(
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
                    sessions = state.recentSessions,
                    onDeleteIconClick = {
                        onEvent(SubjectEvent.OnDeleteSessionButtonClick(it))
                        isDeleteSessionDialogOpen = true
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreenTopBar(
    title: String,
    onBackButtonClick: () -> Unit,
    onDeleteButtonClick: () -> Unit,
    onEditButtonClick: () -> Unit,
    scrollBehavior: TopAppBarScrollBehavior
)
{
    LargeTopAppBar(scrollBehavior = scrollBehavior, navigationIcon = {
        IconButton(
            onClick = onBackButtonClick
        ) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "navigate back"
            )
        }
    }, title = {
        Text(
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.headlineSmall
        )
    }, actions = {
        IconButton(
            onClick = onDeleteButtonClick
        ) {
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Subject"
            )
        }
        IconButton(
            onClick = onEditButtonClick
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = "Edit Subject"
            )
        }
    })
}

@Composable
private fun SubjectOverviewSection(
    modifier: Modifier,
    studiedHours: String,
    goalHours: String,
    progress: Float,
)
{
    val percentageProcess = remember(progress) {
        (progress * 100).toInt().coerceIn(0, 100)
    }

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Goal Study Hours",
            count = goalHours,
        )
        Spacer(modifier = Modifier.width(10.dp))
        CountCard(
            modifier = Modifier.weight(1f),
            headingText = "Studied Hours",
            count = studiedHours,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier.size(75.dp), contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = 1f,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            CircularProgressIndicator(
                modifier = Modifier.fillMaxSize(),
                progress = progress,
                strokeWidth = 4.dp,
                strokeCap = StrokeCap.Round,
            )
            Text(text = "$percentageProcess%")
        }
    }
}