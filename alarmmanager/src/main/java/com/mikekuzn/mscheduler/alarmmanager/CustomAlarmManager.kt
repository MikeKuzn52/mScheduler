package com.mikekuzn.mscheduler.alarmmanager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Named

class CustomAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("AlarmActivityClass") private val activity: Class<*>,
) : CustomAlarmManagerInter {
    private fun getPendingIntent(hashCode: Int) = PendingIntent.getActivity(
        context, hashCode,
        Intent(context, activity).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )

    @SuppressLint("ScheduleExactAlarm", "SimpleDateFormat")
    override fun writeAlarm(hashCode: Int, timeInMillis: Long) {
        Log.d("***[", "writeAlarm ${SimpleDateFormat("yy.MM.dd HH:mm").format(timeInMillis)}")
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(hashCode)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    override fun cancelAlarm(hashCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pendingIntent = getPendingIntent(hashCode)
        alarmManager.cancel(pendingIntent)
    }
}