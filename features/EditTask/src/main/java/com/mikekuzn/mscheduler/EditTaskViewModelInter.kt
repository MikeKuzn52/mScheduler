package com.mikekuzn.mscheduler

import androidx.compose.runtime.State
import com.mikekuzn.mscheduler.entities.Repeat
import com.mikekuzn.mscheduler.entities.Task

interface EditTaskViewModelInter {
    val editTask: State<Task?>
    fun edit(task: Task?)
    fun stopEdit(): Boolean
    fun setTitle(t: String)
    fun setDescription(d: String)
    fun invertUseTime()
    fun changeDataTime(newDataTime: Long)
    fun setRepeat(repeat: Repeat)
}