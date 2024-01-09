package com.mikekuzn.mscheduler.domain

import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mikekuzn.mscheduler.domain.entities.Task

interface UseCasesInter {
    fun setUserPath(userPath: String)
    fun clrUserPath()
    fun getTaskList(): SnapshotStateList<Task>
    fun swap(from: Int, to: Int)
    fun addTask(newTask: Task)
    fun deleteTask(index: Int)
    fun modifyTask(index: Int)
    fun getByHash(hash: Int): List<Task>
    fun setReady(task: Task)
    fun postpone(task: Task, dataTimePostpone: Long)
    fun setNextAlarm()
    val isSigned: Boolean
}