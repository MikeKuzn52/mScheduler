package com.mikekuzn.mscheduler.domain

import com.mikekuzn.mscheduler.domain.entities.Task

interface RepositoryInter {
    fun subscribe(userPath: String, add: (task: Task) -> Unit)
    fun unsubscribe()
    fun add(task: Task): Boolean
    fun getNextKey(): String?
}