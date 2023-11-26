package com.mikekuzn.mscheduler.features.alarmManager

interface CustomAlarmManagerInter {
    fun writeAlarm(hashCode: Int, timeInMillis: Long)
    fun cancelAlarm(hashCode: Int)
}