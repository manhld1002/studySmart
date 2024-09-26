package vn.miagi.studysmart.presentation.subject

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
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
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import vn.miagi.studysmart.domain.model.Subject
import vn.miagi.studysmart.presentation.components.AddSubjectDialog
import vn.miagi.studysmart.presentation.components.CountCard
import vn.miagi.studysmart.presentation.components.DeleteDialog
import vn.miagi.studysmart.presentation.components.StudySessionsList
import vn.miagi.studysmart.presentation.components.TasksList
import vn.miagi.studysmart.presentation.destinations.TaskScreenRouteDestination
import vn.miagi.studysmart.presentation.task.TaskScreenNavArgs
import vn.miagi.studysmart.sessions
import vn.miagi.studysmart.tasks

data class SubjectScreenNavArgs(
    val subjectId: Int
)

@Destination(navArgsDelegate = SubjectScreenNavArgs::class)
@Composable
fun SubjectScreenRoute(
    navigator: DestinationsNavigator
)
{
    val viewModel : SubjectViewModel = hiltViewModel()
    SubjectScreen(
        onBackButtonClick = { navigator.navigateUp() },
        onAddTaskButtonClick = {
            val navArg =
                TaskScreenNavArgs(taskId = null, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
        onTaskCardClick = { taskId ->
            val navArg =
                TaskScreenNavArgs(taskId = taskId, subjectId = null)
            navigator.navigate(TaskScreenRouteDestination(navArgs = navArg))
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubjectScreen(
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

    var subjectName by remember { mutableStateOf("") }
    var goalHours by remember { mutableStateOf("") }
    var selectedColor by remember {
        mutableStateOf(Subject.subjectCardColors.random())
    }

    AddSubjectDialog(
        isOpen = isAddSubjectDialogOpen,
        subjectName = subjectName,
        goalHours = goalHours,
        onSubjectNameChange = { subjectName = it },
        onGoalHoursChange = { goalHours = it },
        selectedColors = selectedColor,
        onColorChange = { selectedColor = it },
        onDismissRequest = {
            isAddSubjectDialogOpen = false
        },
        onConfirmButtonClick = {
            isAddSubjectDialogOpen = false
        },
    )

    DeleteDialog(
        isOpen = isDeleteSessionDialogOpen,
        title = "Delete Subject?",
        bodyText = "Do you want to delete this subject? All related tasks and study sessions will be permanently removed. This action can not be undone.",
        onDismissRequest = { isDeleteSessionDialogOpen = false },
        onConfirmButtonClick = { isDeleteSessionDialogOpen = false }
    )

    Scaffold(
        // notify scroll behavior for the scaffold
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            SubjectScreenTopBar(
                title = "English",
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
            ExtendedFloatingActionButton(
                onClick = onAddTaskButtonClick,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add"
                    )
                },
                text = { Text(text = "Add Task") },
                expanded = isFABExpanded
            )
        }
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
                    studiedHours = "10",
                    goalHours = "15",
                    progress = 0.75f,
                )
            }
            item {
                TasksList(
                    sectionTitle = "UPCOMING TASKS",
                    emptyListText = "You don't have any upcoming tasks. \n Click the + button to add new task",
                    tasksList = tasks,
                    onCheckBoxClick = {},
                    onTaskCardClick = onTaskCardClick,
                )
                Spacer(modifier = Modifier.height(20.dp))
                TasksList(
                    sectionTitle = "COMPLETED TASKS",
                    emptyListText = "You don't have any completed tasks. \n Click the check box on completion of task",
                    tasksList = tasks,
                    onCheckBoxClick = {},
                    onTaskCardClick = onTaskCardClick,
                )
                Spacer(modifier = Modifier.height(20.dp))
                StudySessionsList(
                    sectionTitle = "RECENT STUDY SESSIONS",
                    emptyListText = "You don't have any recent study sessions. \n Start a study session to begin recording your progress",
                    sessions = sessions,
                    onDeleteIconClick = {
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
    LargeTopAppBar(
        scrollBehavior = scrollBehavior,
        navigationIcon = {
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
            headingText = "Study Hours",
            count = studiedHours,
        )
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier.size(75.dp),
            contentAlignment = Alignment.Center
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