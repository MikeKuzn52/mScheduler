package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.entities.Task
interface RepositoryInter {
    fun subscribe(userPath: String, updater: TaskListUpdaterInter)
    fun unsubscribe()
    fun add(task: Task): String?
    fun delete(key: String?): Boolean
    fun modify(key: String, task: Task): Boolean
}

interface TaskListUpdaterInter {
    fun before()
    fun addOrUpdate(task: Task)
    fun after()
}
