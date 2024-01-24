package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.entities.Task

interface SoundTaskInter {
    fun execute(task: Task)
    fun stop()
}