package com.mikekuzn.mscheduler.alarmmanager

import android.content.Intent

interface CustomAlarmManagerInter {
    fun writeAlarm(hashCode: Int, timeInMillis: Long, addData: ((intent: Intent) -> Unit)? = null)
    fun cancelAlarm(hashCode: Int)
}
