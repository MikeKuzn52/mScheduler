package com.mikekuzn.mscheduler.alarmmanager

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.text.SimpleDateFormat
import javax.inject.Inject
import javax.inject.Named

const val TAG = "mScheduler"

class CustomAlarmManager @Inject constructor(
    @ApplicationContext private val context: Context,
    @Named("AlarmClass") private val clazz: Class<*>,
) : CustomAlarmManagerInter {

    private val alarmManager by lazy {
        context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun getPendingIntent(hashCode: Int, addData: ((intent: Intent) -> Unit)? = null) = PendingIntent.getService(
        context, hashCode,
        Intent().apply {
            setComponent(
                ComponentName(context.packageName, clazz.name)
            )
            addData?.invoke(this)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
    )

    @SuppressLint("ScheduleExactAlarm", "SimpleDateFormat")
    override fun writeAlarm(hashCode: Int, timeInMillis: Long, addData: ((intent: Intent) -> Unit)?) {
        Log.d(TAG, "writeAlarm ${SimpleDateFormat("yy.MM.dd HH:mm").format(timeInMillis)}")
        val pendingIntent = getPendingIntent(hashCode, addData)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent)
    }

    override fun cancelAlarm(hashCode: Int) {
        val pendingIntent = getPendingIntent(hashCode)
        alarmManager.cancel(pendingIntent)
    }
}