package com.mikekuzn.mscheduler.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikekuzn.mscheduler.features.editTask.AddTaskUI
import com.mikekuzn.mscheduler.features.editTask.EditTaskUI
import com.mikekuzn.mscheduler.features.editTask.EditTaskViewModel
import com.mikekuzn.mscheduler.features.editTask.EditTaskViewModelInter
import com.mikekuzn.mscheduler.features.signing.SignOut
import com.mikekuzn.mscheduler.features.signing.SigningUI
import com.mikekuzn.mscheduler.features.taskList.TaskListUI
import com.mikekuzn.mscheduler.features.taskList.TaskListViewModel
import com.mikekuzn.mscheduler.features.taskList.TaskListViewModelInter

@Composable
fun RootWindow(
    tlVm: TaskListViewModelInter = hiltViewModel<TaskListViewModel>(),
) {
    if (tlVm.isSigned) {
        WorkWindow()
    } else {
        SigningUI()
    }
}

@Composable
fun WorkWindow(
    vmEdit: EditTaskViewModelInter = hiltViewModel<EditTaskViewModel>(),
) {
    Column {
        AnimatedVisibility(
            visible = vmEdit.editTask.value != null
        ) {
            EditTaskUI()
        }
        TaskListUI(modifier = Modifier.weight(1F))
        Column {
            Divider(thickness = 3.dp, color = Color.Black)
            AnimatedVisibility(
                visible = vmEdit.editTask.value == null
            ) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(start = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    AddTaskUI()
                    SignOut()
                }
            }
        }
    }
}
