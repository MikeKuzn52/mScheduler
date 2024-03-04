package com.mikekuzn.mscheduler

import android.content.Context
import android.util.Log
import com.mikekuzn.mscheduler.alarmmanager.CustomAlarmManagerInter
import com.mikekuzn.mscheduler.entities.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import javax.inject.Inject
import kotlin.math.min

const val TAG = "mScheduler"

class AlarmUseCases @Inject constructor(
    private val taskList: TaskList,
    private val alarmManager: CustomAlarmManagerInter,
    private val getCurrentTime: GetCurrentTime,
    @ApplicationContext private val context: Context,
) : AlarmUseCasesInter, AlarmUseCasesUpdateInter {

    private var next: Pair<Task?, Long> = null to Long.MAX_VALUE

    // Вернуть список из всех задачь с совпадающим временем события (таких может быть несколько)
    override fun getByTime(time: Long?): List<Task> {
        val resultTaskList = mutableListOf<Task>()
        for (task in taskList.getTaskList()) {
            task.timeForeach {
                Log.d(TAG, "getByTime $it =?= $time")
                if (it == time) {
                    resultTaskList.add(task)
                }
            }
        }
        return resultTaskList
    }

    override fun setReady(task: Task) {
        // TODO("Not yet implemented")
    }

    override fun postpone(task: Task, dataTimePostpone: Long) {
        TODO("Not yet implemented")
    }

    override fun setNextAlarm() {
        val currentTime = System.currentTimeMillis()
        var newNext: Pair<Task?, Long> = null to Long.MAX_VALUE
        for (task in taskList.getTaskList()) {
            val time = task.getNext(currentTime)
            if (time < newNext.second) {
                newNext = task to time
            }
        }
        Log.d(TAG, "setNextAlarm ${newNext.first}")
        newNext.first?.let {
            setNext(it, newNext.second)
        }
    }

    override fun updateForTask(task: Task) {
        val systemDataTime = getCurrentTime.execute()
        val newDataTime = task.getNext(systemDataTime)
        if (newDataTime != Long.MAX_VALUE && next.second > newDataTime) {
            // Заменить следующее событие будильника
            if (next.second != Long.MAX_VALUE) {
                alarmManager.cancelAlarm(next.second.hashCode())
            }
            setNext(task, newDataTime)
        }
    }

    private fun setNext(task: Task, newDataTime: Long) {
        alarmManager.writeAlarm(
            newDataTime.hashCode(),
            newDataTime - 30000,
        ) {
            // TODO("Move string constants to separate file")
            it.putExtra("TIME", newDataTime)
            it.putExtra("TITLE", task.title)
        }
        next = task to newDataTime
    }

    override fun updateWhenDelete(task: Task) {
        if (next.first == task) {
            alarmManager.cancelAlarm(next.second.hashCode())
            next = null to Long.MAX_VALUE
            setNextAlarm()
        }
    }

    // Вернуть время следующего после dataTimeLess события или Long.MAX_VALUE
    private fun Task.getNext(dataTimeLess: Long): Long {
        var minDataTime = Long.MAX_VALUE
        if (useTime) {
            timeForeach {
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
