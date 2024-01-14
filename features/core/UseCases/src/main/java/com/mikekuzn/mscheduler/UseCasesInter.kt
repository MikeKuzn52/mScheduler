package com.mikekuzn.mscheduler

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mikekuzn.mscheduler.entities.Task

interface UseCasesInter {
    fun setUserPath(userPath: String)
    fun clrUserPath()

    fun getTaskList(): SnapshotStateList<Task>
    fun swap(from: Int, to: Int)
    fun addTask(newTask: Task)
    fun deleteTask(index: Int)
    fun modifyTask(index: Int)
    fun setAsSubTask(index: Int)

    val isSigned: Boolean
}
