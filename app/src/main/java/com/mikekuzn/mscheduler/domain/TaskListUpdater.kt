package com.mikekuzn.mscheduler.domain

import androidx.compose.runtime.mutableStateListOf
import com.mikekuzn.mscheduler.domain.entities.Task

abstract class TaskListUpdater(
    val taskList: List<Task>,
    val deleteTask: (index: Int) -> Unit
): TaskListUpdaterInter {

    // List of all task which should be processed
    private val taskListToUpdate = mutableStateListOf<String>()

    override fun before() {
        taskListToUpdate.clear()
    }

    override fun addOrUpdate(newTask: Task) {
        taskListToUpdate.add(newTask.key!!)
    }

    override fun after() {
        taskList.forEachIndexed { index, task ->
            if (!taskListToUpdate.any { it == task.key }) {
                deleteTask(index)
            }
        }
    }
}