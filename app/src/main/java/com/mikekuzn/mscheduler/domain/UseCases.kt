package com.mikekuzn.mscheduler.domain

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mikekuzn.mscheduler.alarmmanager.CustomAlarmManagerInter
import com.mikekuzn.mscheduler.domain.entities.Task
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.min

class UseCases @Inject constructor(
    private val alarmManager: CustomAlarmManagerInter,
    private val getCurrentTime: GetCurrentTime,
    private val repository: RepositoryInter,
) : UseCasesInter {

    private val taskListM = mutableStateListOf<Task>()
    private var next: Pair<Task?, Long> = null to Long.MAX_VALUE
    private var savedUserPath by mutableStateOf<String?>(null)

    override fun setUserPath(userPath: String) {
        Log.d("***[", "setUserPath $userPath")
        if (savedUserPath != userPath) {
            savedUserPath?.run { throw Exception("Internal error. Add UserPath") }
            savedUserPath = userPath
            // TODO change subscribe(...) { taskArray[] ->
            //  and remove tasks which key was not found
            repository.subscribe(userPath) { newTask ->
                taskListM.find { it.key == newTask.key }?.let {
                    // TODO check hash sum and change
                } ?: run {
                    taskTimeProcess(newTask)
                    taskListM.add(newTask)

                }
            }
        }
    }

    override fun clrUserPath() {
        this.savedUserPath = null
        repository.unsubscribe()
        taskListM.clear()
    }

    override fun getTaskList(): SnapshotStateList<Task> = taskListM

    override fun swap(from: Int, to: Int) {
        if (from == to) return
        // swap key values to swap on firebase
        taskListM[to].key = taskListM[from].key.also { taskListM[from].key = taskListM[to].key }
        modifyTask(to)
        modifyTask(from)
        // swap on local array
        taskListM.add(to, taskListM.removeAt(from))
    }

    // Add form UI
    override fun addTask(newTask: Task) {
        taskTimeProcess(newTask)
        // if newTask.key == null it is local task
        newTask.key ?: let {
            val newKey = repository.add(newTask)
            Log.d("***[", "addTask key='${newTask.key}->$newKey")
            newTask.key = newKey
            if (newKey.isNullOrEmpty()) {
                // TODO newKey == null -> Toast or anything also and repeat to write
            }
            else if (!taskListM.any{ it.key == newKey }) {
                taskListM.add(newTask)
            }
        }
    }

    override fun deleteTask(index: Int) {
        // TODO("make suspend")
        repository.delete(taskListM[index].key)
        taskListM.removeAt(index)
    }

    override fun modifyTask(index: Int) {
        if (taskListM[index].key.isNullOrEmpty()) {
            Log.e("***[", "modifyTask N$index key='${taskListM[index].key}")
            // TODO Toast or anything also
            return
        }
        // TODO("make suspend")
        repository.modify(taskListM[index].key!!, taskListM[index])
    }

    private fun taskTimeProcess(newTask: Task) {
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

fun Task.timeForeach(handle: (currentDataTime: Long) -> Unit) {
    // TODO not yet implemented Repeat (not only Once)
    for (before in minutesBefore) {
        val currentDataTime = dataTime - before * 1000
        handle(currentDataTime)
    }
}


class GetCurrentTime @Inject constructor() {
    fun execute() = Calendar.getInstance().timeInMillis
}
