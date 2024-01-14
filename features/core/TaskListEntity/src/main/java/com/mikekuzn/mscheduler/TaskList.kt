package com.mikekuzn.mscheduler

import androidx.compose.runtime.mutableStateListOf
import com.mikekuzn.mscheduler.entities.Task
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TaskList @Inject constructor() {
    private val taskListM = mutableStateListOf<Task>()

    fun getTaskList() = taskListM
}