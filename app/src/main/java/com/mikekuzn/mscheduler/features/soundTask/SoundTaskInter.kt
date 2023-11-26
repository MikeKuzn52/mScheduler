package com.mikekuzn.mscheduler.features.soundTask

import com.mikekuzn.mscheduler.domain.entities.Task

interface SoundTaskInter {
    fun soundTask(task: Task)
}