package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.entities.Task
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

    override suspend fun add(task: Task) = db?.add(task.toTaskData())

    override suspend fun delete(key: String?): Boolean {
        if (!key.isNullOrEmpty()) {
            return db?.delete(key)?: false
        }
        return false
    }

    override suspend fun modify(key: String, task: Task): Boolean {
        if (db != null && key.isNotEmpty()) {
            return db!!.modify(key, task.toTaskData())
        }
        return false
    }

    private fun TaskData.toTask(key: String) = Task(
        title = title,
        description = description,
        haveChild = haveChild,
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
        haveChild = haveChild,
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
