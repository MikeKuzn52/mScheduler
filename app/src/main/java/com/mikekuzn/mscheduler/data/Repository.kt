package com.mikekuzn.mscheduler.data

import com.mikekuzn.mscheduler.domain.RepositoryInter
import com.mikekuzn.mscheduler.domain.entities.Task
import javax.inject.Inject

class Repository @Inject constructor(
    private val dBFactory: FirebaseDBFactory
) : RepositoryInter {
    var db: FirebaseDB? = null

    override fun subscribe(userPath: String, add: (task: Task) -> Unit) {
        db = dBFactory.create(userPath)
        /*db!!.subscribe { taskData: TaskData ->
            add(taskData.toTask())
        } // */
    }

    override fun unsubscribe() {
        db?.unsubscribe()
        db = null
    }

    override fun add(task: Task) = db?.add(task.toTaskData()) ?: false

    override fun getNextKey() = db?.getNextKey()


    private fun TaskData.toTask() = Task(
        key = key,
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

    private fun Task.toTaskData() = TaskData(
        key = key,
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