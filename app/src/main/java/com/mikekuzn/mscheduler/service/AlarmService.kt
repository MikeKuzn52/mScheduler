package com.mikekuzn.mscheduler.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import com.mikekuzn.mscheduler.SoundTaskInter
import com.mikekuzn.mscheduler.entities.Task
import com.mikekuzn.mscheduler.presentation.AlarmActivity
import com.mikekuzn.resource.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val TAG = "mScheduler"
const val ACTION = "_Action_"

enum class Action { Finish, Postpone, Close }

//private const val REQUEST_CODE_FINISH = 101
private const val REQUEST_CODE_POSTPONE = 102

@AndroidEntryPoint
class AlarmService : Service() {
    // TODO("move SharedPreferences to separate file/module")
    // TODO("Запланировано использовать 2 кнопки в шторке: finishEvent и postponeEvent
    //  finishEvent завершить событие и если оно имеет признак haveChild то пометить check.
    //  postponeEvent открытить AlarmActivity в которой будет можно выбрать закончить событие или
    //     перенести на позднее время (отсрочить)
    //  Но пока реализовано только открытие AlarmActivity")

    private val foregroundHelper = ForegroundHelper(this)

    @Inject
    lateinit var soundTask: SoundTaskInter

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val actionString = intent?.getStringExtra(ACTION)
        val extras = intent?.extras
        if (actionString != null && extras == null){
            // Click on foreground button
            // TODO("Not yet implemented")
            stopService()
            //when (actionString) {
            //    Action.Finish.name -> {
            //        Log.d(TAG, "AlarmService. Finish command")
            //        foregroundHelper.stopForegroundService()
            //    }
            //    Action.Close.name -> {
            //        Log.d(TAG, "AlarmService. Close command")
            //        foregroundHelper.stopForegroundService()
            //    }
            //    else -> Log.e(TAG, "AlarmService. Unknown command=$actionString")
            //}
        } else if (extras != null) {
            // TODO("Move string constants to separate file")
            val time = extras.getLong("TIME") as Long?
            val defTitle = getString(R.string.defaultNotification)
            val title = extras.getString("TITLE", defTitle)
            Log.d(TAG, "AlarmService start time=$time title=$title")
            if (time == null) {
                stopService()
            } else {
                foregroundServiceLogic(time, title)
                return START_REDELIVER_INTENT
            }
        } else {
            Log.e(TAG, "AlarmService. Unknown intent=$intent")
            stopService()

        }
        return START_NOT_STICKY
    }

    private fun stopService() {
        soundTask.stop()
        stopSelf()
    }

    private fun foregroundServiceLogic(actionTime: Long, title: String) {
        val currentTime = System.currentTimeMillis()
        if (actionTime in (currentTime - 2 * 60 - 1000)..(currentTime + 2 * 60 - 1000)) {
            Log.d(TAG, "AlarmService. Incorrect time=$actionTime current=$currentTime")
            stopService()
            return
        }
        startForegroundService(actionTime, title)
        object: CountDownTimer(actionTime - currentTime, 1000) {
            override fun onTick(diffTime: Long) {
                foregroundHelper.updateNotificationText("${getString(R.string.timeToEvent)} ${diffTime / 1000}")
            }
            override fun onFinish() {
                foregroundHelper.updateNotificationText(null)
                soundTask.execute(Task()) // TODO(" Task() -> real data")
            }
        }.start()
    }

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService(hashCode: Long, text: String) {
        foregroundHelper.startForegroundService {
            it
                .setContentTitle(text)
                .setSmallIcon(R.drawable.i_alarm)
                .setAutoCancel(true)
                //.addAction(0, getString(R.string.finishEvent), finishPendingIntent())
                //.addAction(0, getString(R.string.postponeEvent), activityPendingIntent())
                .setContentIntent(activityPendingIntent(hashCode))
        }
    }

    //private fun finishPendingIntent(t) = PendingIntent.getService(
    //    this,
    //    REQUEST_CODE_FINISH,
    //    Intent(this, AlarmService::class.java).apply {
    //        putExtra(ACTION, Action.Finish.name)
    //    },
    //    PendingIntent.FLAG_IMMUTABLE
    //)

    private fun activityPendingIntent(hashCode: Long) = PendingIntent.getActivity(
        this,
        REQUEST_CODE_POSTPONE,
        Intent(this, AlarmActivity::class.java).apply {
            putExtra(ACTION, Action.Postpone.name)
            // TODO("Move string constants to separate file")
            putExtra("ACTION_TIME", hashCode)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}
