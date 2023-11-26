package com.mikekuzn.mscheduler.features.editTask

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.repeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikekuzn.mscheduler.R
import com.mikekuzn.mscheduler.domain.entities.repeats
import com.mikekuzn.mscheduler.features.taskList.TaskListViewModel
import com.mikekuzn.mscheduler.features.taskList.TaskListViewModelInter
import com.mikekuzn.mscheduler.features.editTask.dateTimePicker.LocalDateTimePickerProvider
import java.text.SimpleDateFormat
import java.util.Calendar

@SuppressLint("SimpleDateFormat")
@Composable
fun EditTaskUI(
    vm: EditTaskViewModelInter = hiltViewModel<EditTaskViewModel>(),
    tlVm: TaskListViewModelInter = hiltViewModel<TaskListViewModel>(),
    ) {
    val dateTimePickerProvider = LocalDateTimePickerProvider.current
    val editTask = vm.editTask.value
    val titleEnterError = remember { mutableStateOf(false) }

    val editModifier = Modifier
        .fillMaxWidth()
        .padding(bottom = 5.dp)
        .background(color = MaterialTheme.colorScheme.background)
    if (editTask == null) {
        Text(text = stringResource(id = R.string.nullTaskEdit))
    } else {
        Column(
            Modifier
                .background(Color(0xFFCCCCDD))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF5727DD)),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Image(
                    painter = painterResource(id = R.drawable.i_back),
                    contentDescription = "GoToBack",
                    modifier = Modifier.clickable { vm.stopEdit() }
                )
                Text(
                    text = stringResource(id = R.string.editBack),
                    color = Color.White,
                    fontSize = dimensionResource(R.dimen.headFontSize).value.sp,
                )
                Image(
                    painter = painterResource(id = R.drawable.i_apply),
                    contentDescription = "Apply new task",
                    modifier = Modifier.clickable {
                        if (editTask.title.isNotEmpty()) {
                            tlVm.addTask(editTask)
                            vm.stopEdit()
                        } else {
                            titleEnterError.value = true
                        }
                    },
                    alignment = Alignment.CenterEnd,
                )
            }
            InputText(
                editTask.title,
                stringResource(R.string.title),
                true,
                editModifier,
                titleEnterError,
            ) {
                titleEnterError.value = false
                vm.setTitle(it)
            }
            InputText(
                editTask.description,
                stringResource(R.string.description),
                false,
                editModifier
            ) {
                vm.setDescription(it)
            }
            Row(
                modifier = editModifier.padding(start = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.i_time),
                    contentDescription = "TimeSettings"
                )
                Switch(
                    checked = editTask.useTime,
                    modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                    onCheckedChange = { vm.invertUseTime() }
                )
                if (editTask.useTime) {
                    ShowDataTime(stringResource(id = R.string.dataFormat), editTask.dataTime) {
                        val c =  Calendar.getInstance().apply { timeInMillis = editTask.dataTime }
                        dateTimePickerProvider.get()
                            .showDataPicker(c) {
                            vm.changeDataTime(it.timeInMillis)
                        }
                    }
                    ShowDataTime(stringResource(id = R.string.timeFormat), editTask.dataTime) {
                        val c =  Calendar.getInstance().apply { timeInMillis = editTask.dataTime }
                        dateTimePickerProvider.get()
                            .showTimePicker(c) {
                            vm.changeDataTime(it.timeInMillis)
                        }
                    }
                }
            }
            AnimatedVisibility(
                visible = editTask.useTime
            ) {
                Row(
                    modifier = editModifier.padding(start = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.i_repeat),
                        contentDescription = "RepeatEventSettings"
                    )
                    Text(
                        text = stringResource(id = R.string.repeatEvent),
                        fontSize = dimensionResource(R.dimen.normalFontSize).value.sp,
                    )
                    CustomDropdownMenu(editTask.repeat, repeats) { vm.setRepeat(it) }
                }
            }
        }
    }
}

@SuppressLint("SimpleDateFormat")
@Composable
fun ShowDataTime(format: String, dataTime: Long, onClick: () -> Unit) {
    Text(
        text = SimpleDateFormat(format).format(dataTime),
        fontSize = dimensionResource(R.dimen.normalFontSize).value.sp,
        modifier = Modifier
            .clickable(enabled = true, onClick = onClick)
            .padding(start = 10.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputText(
    text: String,
    label: String,
    singleLine: Boolean,
    modifier: Modifier,
    error: MutableState<Boolean> = mutableStateOf(false),
    onChange: (v: String) -> Unit
) {
    val containerColorBlinker by animateColorAsState(
        if (error.value) Color.Red else MaterialTheme.colorScheme.background,
        label = "Enter text",
        animationSpec = repeatable(
            iterations = 3,
            animation = tween(170, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    ) {
        error.value = false
    }
    val fieldColors = TextFieldDefaults.textFieldColors(
        containerColor = containerColorBlinker,
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
    )
    var focused by remember { mutableStateOf(false) }
    TextField(
        modifier = modifier.onFocusChanged { focused = it.isFocused },
        value = text,
        singleLine = singleLine,
        colors = fieldColors,
        onValueChange = onChange,
        label = { if (!focused && text.isEmpty()) Text(label) },
    )
}

