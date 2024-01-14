package com.mikekuzn.mscheduler

import android.util.Log
import com.mikekuzn.mscheduler.alarmmanager.CustomAlarmManagerInter
import com.mikekuzn.mscheduler.entities.Task
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.min

class AlarmUseCases @Inject constructor(
    private val taskList: TaskList,
    private val alarmManager: CustomAlarmManagerInter,
    private val getCurrentTime: GetCurrentTime,
) : AlarmUseCasesInter, AlarmUseCasesUpdateInter {

    private var next: Pair<Task?, Long> = null to Long.MAX_VALUE

    override fun getByHash(hash: Int): List<Task> {
        val resultTaskList = mutableListOf<Task>()
        for (task in taskList.getTaskList()) {
            task.timeForeach {
                Log.d("***[", "getByHash $it =?= $hash")
                if (it.hashCode() == hash) {
                    resultTaskList.add(task)
                }
            }
        }
        return resultTaskList
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

    override fun updateForTask(task: Task) {
        val systemDataTime = getCurrentTime.execute()
        val newDataTime = getNext(
            task,
            systemDataTime
        )
        if (newDataTime != Long.MAX_VALUE && next.second > newDataTime) {
            // Заменить следующее событие будильника
            if (next.second != Long.MAX_VALUE) {
                alarmManager.cancelAlarm(next.second.hashCode())
            }
            alarmManager.writeAlarm(newDataTime.hashCode(), newDataTime)
            next = task to newDataTime
        }
    }

    override fun updateWhenDelete(task: Task) {
        if (next.first == task) {
            // TODO("localDeleteTask need set time for next task")
        }
    }

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

    private fun Task.timeForeach(handle: (currentDataTime: Long) -> Unit) {
        // TODO not yet implemented Repeat (not only Once)
        for (before in minutesBefore) {
            val currentDataTime = dataTime - before * 1000
            handle(currentDataTime)
        }
    }
}

class GetCurrentTime @Inject constructor() {
    fun execute() = Calendar.getInstance().timeInMillis
}