package com.mikekuzn.mscheduler

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mikekuzn.mscheduler.entities.Task

interface UseCasesInter {
    fun setUserPath(userPath: String)
    fun clrUserPath()

    fun getTaskList(): SnapshotStateList<Task>
    suspend fun swap(from: Int, to: Int)
    suspend fun addTask(newTask: Task)
    suspend fun deleteTask(index: Int)
    suspend fun modifyTask(index: Int)
    suspend fun setAsSubTask(index: Int)

    val isSigned: Boolean
}
