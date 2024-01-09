package com.mikekuzn.mscheduler.features.taskList

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikekuzn.mscheduler.R
import com.mikekuzn.mscheduler.domain.entities.Repeat
import com.mikekuzn.mscheduler.domain.entities.Task
import com.mikekuzn.mscheduler.features.custom_list.DragDropSwipeScrollList
import com.mikekuzn.mscheduler.features.custom_list.SwipeState
import java.text.SimpleDateFormat

@Composable
fun TaskListUI(
    modifier: Modifier = Modifier,
    vm: TaskListViewModelInter = hiltViewModel<TaskListViewModel>()
) {
    if (vm.taskList.isEmpty()) {
        Text(
            text = stringResource(id = R.string.noAnyTasks),
            modifier
                .fillMaxSize()
                .padding(20.dp),
            textAlign = TextAlign.Center,
        )
    } else {
        DragDropSwipeScrollList(
            mItems = vm.taskList,
            onSwap = { from, to -> vm.swap(from, to) },
            onTree = { index -> vm.setAsSubTask(index) },
            onDelete = { index -> vm.deleteTask(index) },
            modifier = modifier.fillMaxSize(1F),
            showItem = TaskUI,
            indicatorContent = ScrollIndicator,
            divider = divider,
            dragInductor = DragInductor,
        )
    }
}


private val divider: @Composable () -> Unit = {
    Divider(color = colorResource(id = R.color.defaultHint))
}

private val DragInductor: @Composable (task: Task, swipeState: SwipeState, modifier: Modifier) -> Unit =
    { task, swipeState, modifier ->
        if (swipeState == SwipeState.SUBTASK) {
            Image(
                painter = painterResource(id = R.drawable.i_tree),
                contentDescription = "tree of subtasks",
                modifier = modifier.padding(4.dp),
            )
        } else if (swipeState == SwipeState.DELETE) {
            Image(
                painter = painterResource(id = R.drawable.i_delete),
                contentDescription = "delete the subtasks",
                modifier = modifier.padding(4.dp),
            )
        }
        Image(
            painter = painterResource(id = R.drawable.i_drag),
            contentDescription = "DragTask",
            modifier = modifier.padding(4.dp),
        )
        if (task.parentKey !== null) {
            Log.d("***[", "TODO show subtask")// TODO
        }
    }

private val ScrollIndicator: @Composable (index: Int, maxIndex: Int, isThumbSelected: Boolean) -> Unit =
    { index, maxIndex, isThumbSelected ->
        if (maxIndex > 100) {
            Text(
                text = "${index + 1}",
                Modifier
                    .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 20.dp))
                    .background(Color.Green)
                    .padding(8.dp)
                    .clip(CircleShape)
                    .background(if (isThumbSelected) Color.Blue else Color.White)
                    .padding(8.dp),
            )
        }
    }

@SuppressLint("SimpleDateFormat")
val TaskUI: @Composable (task: Task) -> Unit = { task ->
    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1F)) {
            Text(
                text = task.title,
                fontSize = dimensionResource(R.dimen.normalFontSize).value.sp,
                maxLines = 1,
            )
            Text(
                text = task.description,
                fontSize = dimensionResource(R.dimen.microFontSize).value.sp,
                color = Color.Gray,
                maxLines = 1,
            )

        }
        if (task.useTime) {
            Spacer(modifier = Modifier.width(4.dp))
            Row {
                Column {
                    Image(
                        painter = painterResource(id = R.drawable.i_time_mini),
                        contentDescription = "TimeSettings",
                    )
                    if (task.repeat != Repeat.Once) {
                        Image(
                            painter = painterResource(id = R.drawable.i_repeat_mini),
                            contentDescription = "TimeSettings",
                        )
                    }
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = SimpleDateFormat(stringResource(id = R.string.dataShortFormat))
                            .format(task.dataTime),
                        overflow = TextOverflow.Ellipsis,
                        fontSize = dimensionResource(R.dimen.miniFontSize).value.sp,
                    )
                    Text(
                        text = SimpleDateFormat(stringResource(id = R.string.timeFormat))
                            .format(task.dataTime),
                        overflow = TextOverflow.Ellipsis,
                        fontSize = dimensionResource(R.dimen.miniFontSize).value.sp,
                    )
                }
            }
        }
    }
}
