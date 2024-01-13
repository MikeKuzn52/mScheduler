package com.mikekuzn.mscheduler.domain

import com.mikekuzn.mscheduler.domain.entities.Task

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