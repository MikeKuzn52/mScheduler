package com.mikekuzn.mscheduler.features.editTask

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mikekuzn.mscheduler.R

@Composable
fun AddTaskUI(
    vm: EditTaskViewModelInter = hiltViewModel<EditTaskViewModel>()
) {
    Image(
        painter = painterResource(R.drawable.i_add),
        contentDescription = "Add new task",
        Modifier
            .clickable { vm.edit(null) }
            .size(50.dp)
    )
}
