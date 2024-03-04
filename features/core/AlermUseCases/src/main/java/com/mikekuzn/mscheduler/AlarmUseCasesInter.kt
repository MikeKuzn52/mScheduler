package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.entities.Task

interface AlarmUseCasesInter {
    fun getByTime(time: Long?): List<Task>
    fun setReady(task: Task)
    fun postpone(task: Task, dataTimePostpone: Long)
    fun setNextAlarm()
}