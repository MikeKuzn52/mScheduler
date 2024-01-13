package com.mikekuzn.mscheduler.data

import com.mikekuzn.mscheduler.domain.RepositoryInter
import com.mikekuzn.mscheduler.domain.TaskListUpdaterInter
import com.mikekuzn.mscheduler.domain.entities.Task
import javax.inject.Inject

class Repository @Inject constructor(
    private val dBFactory: FirebaseDBFactory
) : RepositoryInter {
    private var db: FirebaseDB? = null

    override fun subscribe(userPath: String, updater: TaskListUpdaterInter) {
        db = dBFactory.create(userPath)
        db!!.subscribe(
            before = { updater.before() },
            add = { key: String, taskData: TaskData ->
                updater.addOrUpdate(taskData.toTask(key))
            },
            after = {updater.after()},
        )
    }

    override fun unsubscribe() {
        db?.unsubscribe()
        db = null
    }

    override fun add(task: Task) = db?.add(task.toTaskData())

    override fun delete(key: String?): Boolean {
        // TODO("make suspend")
        if (!key.isNullOrEmpty()) {
            db?.delete(key)
            return true
        }
        return false
    }

    override fun modify(key: String, task: Task): Boolean {
        // TODO("make suspend")
        if (db != null && !key.isNullOrEmpty()) {
            db!!.modify(key, task.toTaskData())
            return true
        }
        return false
    }

    private fun TaskData.toTask(key: String) = Task(
        title = title,
        description = description,
        childCount = childCount,
        parentKey = parentKey,
        useTime = useTime,
        dataTime = dataTime,
        // TODO repeat = repeat,
        minutesBefore = minutesBefore,
        colorValue = colorValue,
        checked = checked,
        check = check,
        isSystemMelody = isSystemMelody,
        // TODO melody = melody,
    ).apply { this.key = key }

    private fun Task.toTaskData() = TaskData(
        title = title,
        description = description,
        childCount = childCount,
        parentKey = parentKey,
        useTime = useTime,
        dataTime = dataTime,
        // TODO repeat = repeat,
        minutesBefore = minutesBefore,
        colorValue = colorValue,
        checked = checked,
        check = check,
        isSystemMelody = isSystemMelody,
        // TODO melody = melody,
    )
}