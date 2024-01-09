package com.mikekuzn.mscheduler.alarmmanager

interface CustomAlarmManagerInter {
    fun writeAlarm(hashCode: Int, timeInMillis: Long)
    fun cancelAlarm(hashCode: Int)
}
