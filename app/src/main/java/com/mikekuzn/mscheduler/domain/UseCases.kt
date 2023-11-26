package com.mikekuzn.mscheduler.domain

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.mikekuzn.mscheduler.features.alarmManager.CustomAlarmManagerInter
import com.mikekuzn.mscheduler.domain.entities.Task
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.min

class UseCases @Inject constructor(
    private val alarmManager: CustomAlarmManagerInter,
    private val getCurrentTime: GetCurrentTime,
    private val repository: RepositoryInter
) : UseCasesInter {

    private val taskListM = mutableStateListOf<Task>()
    private var next: Pair<Task?, Long> = null to Long.MAX_VALUE
    private var savedUserPath by mutableStateOf<String?>(null)

    override fun setUserPath(userPath: String) {
        Log.d("***[", "setUserPath $userPath")
        if (this.savedUserPath != userPath) {
            this.savedUserPath = userPath
            repository.subscribe(userPath) { newTask ->
                taskTimeProse(newTask)
                taskListM.add(newTask)
            }
        }
    }

    override fun clrUserPath() {
        this.savedUserPath = null
        repository.unsubscribe()
        taskListM.clear()
    }

    override fun getTaskList(): List<Task> = taskListM

    override fun swap(from: Int, to: Int) {
        if (from == to) return
        taskListM.add(to, taskListM.removeAt(from))
    }

    // Add form UI
    override fun addTask(newTask: Task) {
        taskTimeProse(newTask)
        newTask.key = repository.getNextKey()
        taskListM.add(newTask)
        if (!repository.add(newTask)) {
            newTask.key = null
        }
    }

    override fun deleteTask(index: Int) {
        TODO("Not yet implemented")
    }

    private fun taskTimeProse(newTask: Task) {
        val systemDataTime = getCurrentTime.execute()
        val newDataTime = getNext(newTask, systemDataTime)
        if (newDataTime != Long.MAX_VALUE && next.second > newDataTime) {
            // Заменить следующее событие будильника
            if (next.second != Long.MAX_VALUE) {
                alarmManager.cancelAlarm(next.second.hashCode())
            }
            alarmManager.writeAlarm(newDataTime.hashCode(), newDataTime)
            next = newTask to newDataTime
        }
    }

    override fun getByHash(hash: Int): List<Task> {
        val taskList = mutableListOf<Task>()
        for (task in taskListM) {
            task.timeForeach {
                Log.d("***[", "getByHash $it =?= $hash")
                if (it.hashCode() == hash) {
                    taskList.add(task)
                }
            }
        }
        return taskList
    }

    override fun setReady(task: Task) {
        TODO("Not yet implemented")
    }

    override fun postpone(task: Task, dataTimePostpone: Long) {
        TODO("Not yet implemented")
    }

    override fun setNextAlarm() {
        TODO("Not yet implemented")
    }

    override val isSigned get() = this.savedUserPath != null

    // Вернуть время следующего после dataTimeLess события или Long.MAX_VALUE
    private fun getNext(task: Task, dataTimeLess: Long): Long {
        var minDataTime = Long.MAX_VALUE
        if (task.useTime) {
            task.timeForeach {
                if (it > dataTimeLess) {
                    minDataTime = min(it, minDataTime)
                }
            }
        }
        return minDataTime
    }
}

fun Task.timeForeach(pros: (currentDataTime: Long) -> Unit) {
    // TODO not yet implemented Repeat. not Once
    for (before in minutesBefore) {
        val currentDataTime = dataTime - before * 1000
        pros(currentDataTime)
    }
}


class GetCurrentTime @Inject constructor() {
    fun execute() = Calendar.getInstance().timeInMillis
}
