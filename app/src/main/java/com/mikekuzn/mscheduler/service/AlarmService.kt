package com.mikekuzn.mscheduler.service

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.mikekuzn.mscheduler.SoundTaskInter
import com.mikekuzn.mscheduler.entities.Task
import com.mikekuzn.mscheduler.presentation.AlarmActivity
import com.mikekuzn.resource.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

const val ACTION = "_Action_"

enum class Action { Finish, Postpone, Close }

private const val REQUEST_CODE_FINISH = 101
private const val REQUEST_CODE_POSTPONE = 102

@AndroidEntryPoint
class AlarmService : Service() {
    private val foregroundHelper = ForegroundHelper(this)

    @Inject
    lateinit var soundTask: SoundTaskInter

    override fun onCreate() {
        super.onCreate()
        startForegroundService(getString(R.string.defaultNotification))
        soundTask.execute(Task())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val s = intent?.getStringExtra(ACTION)
        if (s != null) {
            soundTask.stop()
        }
        when (s) {
            Action.Finish.name -> {
                Log.d("***[", "AlarmService. Finish command")
                // TODO
                foregroundHelper.stopForegroundService()
            }
            Action.Close.name -> {
                Log.d("***[", "AlarmService. Close command")
                foregroundHelper.stopForegroundService()
            }
            null -> Log.d("***[", "AlarmService start")
            else -> Log.d("***[", "AlarmService. Unknown command=$s")
        }
        return START_REDELIVER_INTENT
    }

    override fun onBind(intent: Intent): IBinder? = null

    @SuppressLint("ForegroundServiceType")
    private fun startForegroundService(text: String) {
        foregroundHelper.startForegroundService {
            it
                .setContentTitle(text)
                .setSmallIcon(R.drawable.i_alarm)
                .setAutoCancel(true)
                .addAction(0, getString(R.string.finishEvent), finishPendingIntent())
                .addAction(0, getString(R.string.postponeEvent), activityPendingIntent())
                .setContentIntent(activityPendingIntent())
        }
    }

    private fun finishPendingIntent() = PendingIntent.getService(
        this,
        REQUEST_CODE_FINISH,
        Intent(this, AlarmService::class.java).apply {
            putExtra(ACTION, Action.Finish.name)
        },
        PendingIntent.FLAG_IMMUTABLE
    )

    private fun activityPendingIntent() = PendingIntent.getActivity(
        this,
        REQUEST_CODE_POSTPONE,
        Intent(this, AlarmActivity::class.java).apply {
            putExtra(ACTION, Action.Postpone.name)
        },
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )
}
