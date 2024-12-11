package com.mikekuzn.mscheduler.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikekuzn.mscheduler.AddTaskUI
import com.mikekuzn.mscheduler.AlarmUseCases
import com.mikekuzn.mscheduler.EditTaskUI
import com.mikekuzn.mscheduler.EditTaskViewModel
import com.mikekuzn.mscheduler.EditTaskViewModelInter
import com.mikekuzn.mscheduler.SignOut
import com.mikekuzn.mscheduler.SigningUI
import com.mikekuzn.mscheduler.TaskListUI
import com.mikekuzn.mscheduler.TaskListViewModel
import com.mikekuzn.mscheduler.TaskListViewModelInter
import com.mikekuzn.mscheduler.alarmmanager.CustomAlarmManager
import com.mikekuzn.mscheduler.service.AlarmService
import java.text.SimpleDateFormat

@Composable
fun RootWindow(
    tlVm: TaskListViewModelInter = hiltViewModel<TaskListViewModel>(),
) {
    //*
    val context = LocalContext.current
    val newDataTime = remember { mutableLongStateOf(0) }
    fun setAlarmPlusTime(add: Long) {
        val alarmManager = CustomAlarmManager(context, AlarmService::class.java)
        newDataTime.longValue = System.currentTimeMillis() / 60000 * 60000 + add
        alarmManager.writeAlarm(
            newDataTime.hashCode(),
            newDataTime.longValue,
        ) {
            it.putExtra("TIME", newDataTime.longValue)
            it.putExtra("TITLE", "Test event")
        }
    }
    Column {
        Button(onClick = {
            setAlarmPlusTime(60000)
        }) {
            Text(text = "Test event +1мин")
        }
        Button(onClick = {
            setAlarmPlusTime(60000*2)
        }) {
            Text(text = "Test event +2мин")
        }
        Button(onClick = {
            setAlarmPlusTime(60000*5)
        }) {
            Text(text = "Test event +5мин")
        }
        Text(text = "NexTime is ${SimpleDateFormat("yy.MM.dd HH:mm").format(newDataTime.longValue)}")
        if (newDataTime.longValue % 60000 != 0L) {
            Text(text = "Время не кратно минуте, есть доли минуты")
        }
    }
    // */
    /*
    if (tlVm.isSigned) {
        WorkWindow()
    } else {
        SigningUI()
    }
    // */
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
