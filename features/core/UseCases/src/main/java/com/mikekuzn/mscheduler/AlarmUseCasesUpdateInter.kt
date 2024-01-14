package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.entities.Task

interface AlarmUseCasesUpdateInter {
    fun updateForTask(task: Task)
    fun updateWhenDelete(task: Task)
}