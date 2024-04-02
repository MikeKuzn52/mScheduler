package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.entities.Task
interface RepositoryInter {
    fun subscribe(userPath: String, updater: TaskListUpdaterInter)
    fun unsubscribe()
    suspend fun add(task: Task): String?
    suspend fun delete(key: String?): Boolean
    suspend fun modify(key: String, task: Task): Boolean
}

interface TaskListUpdaterInter {
    fun before()
    fun addOrUpdate(task: Task)
    fun after()
}
