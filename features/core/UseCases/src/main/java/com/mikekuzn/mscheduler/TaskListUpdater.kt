package com.mikekuzn.mscheduler

import androidx.compose.runtime.mutableStateListOf
import com.mikekuzn.mscheduler.entities.Task

abstract class TaskListUpdater(
    val taskList: List<Task>,
    val deleteTask: (index: Int) -> Unit
): TaskListUpdaterInter {

    // List of all task which should be processed
    private val taskListToUpdate = mutableStateListOf<String>()

    override fun before() {
        taskListToUpdate.clear()
    }

    override fun addOrUpdate(task: Task) {
        taskListToUpdate.add(task.key!!)
    }

    override fun after() {
        taskList.forEachIndexed { index, task ->
            if (!taskListToUpdate.any { it == task.key }) {
                deleteTask(index)
            }
        }
    }
}