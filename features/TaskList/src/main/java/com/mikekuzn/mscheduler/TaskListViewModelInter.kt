package com.mikekuzn.mscheduler

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mikekuzn.mscheduler.entities.Task

interface TaskListViewModelInter {
    val taskList: SnapshotStateList<Task>
    fun swap(from: Int, to: Int)
    fun addTask(newTask: Task)
    fun setAsSubTask(index: Int)
    fun deleteTask(index: Int)
    val isSigned: Boolean
}
