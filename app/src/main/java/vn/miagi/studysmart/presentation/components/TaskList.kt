package vn.miagi.studysmart.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import vn.miagi.studysmart.R
import vn.miagi.studysmart.domain.model.Task
import vn.miagi.studysmart.util.Priority

@Composable
fun TasksList(
    sectionTitle: String,
    emptyListText: String,
    tasksList: List<Task>,
    onTaskCardClick: (Int?) -> Unit,
    onCheckBoxClick: (Task) -> Unit
)
{
    Column {
        Text(
            text = sectionTitle,
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(12.dp)
        )

        if (tasksList.isEmpty())
        {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .size(120.dp),
                    painter = painterResource(id = R.drawable.img_books),
                    contentDescription = emptyListText,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = emptyListText,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            }
        }
        for (task in tasksList)
        {
            TaskCard(
                modifier = Modifier.padding(
                    horizontal = 12.dp,
                    vertical = 4.dp
                ),
                task = task,
                onCheckBoxClick = {
                    onCheckBoxClick(task)
                },
                onClick = {
                    onTaskCardClick(task.taskId)
                }
            )
        }

        // You can add additional tasks here if the list is not empty

    }

}

@Composable
private fun TaskCard(
    modifier: Modifier = Modifier,
    task: Task,
    onCheckBoxClick: () -> Unit,
    onClick: () -> Unit,
)
{
    ElevatedCard(
        modifier = modifier.clickable {
            onClick()
        }
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TaskCheckBox(
                isComplete = task.isComplete,
                borderColor = Priority.fromInt(task.priority).color,
                onCheckBoxClick = {
                    onCheckBoxClick()
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = task.title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isComplete)
                    {
                        TextDecoration.LineThrough
                    } else
                    {
                        TextDecoration.None
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${task.dueDate}",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}
