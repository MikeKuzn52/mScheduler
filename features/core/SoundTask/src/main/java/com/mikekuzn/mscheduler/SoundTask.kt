package com.mikekuzn.mscheduler

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Log
import com.mikekuzn.mscheduler.entities.Task
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

const val TAG = "mScheduler"

@Singleton // to save last "ringtone" object
class SoundTask @Inject constructor(
    @ApplicationContext private val context: Context,
) : SoundTaskInter {

    private var ringtone: Ringtone? = null

    override fun execute(task: Task) {
        Log.d(TAG, "SoundTask play ${task.title}")
        ringtone?.stop() // Stop old playing
        ringtone = if (task.isSystemMelody) {
            RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ) ?: RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            )
        } else {
            RingtoneManager.getRingtone(context, task.melody)
        }
        ringtone?.play()
    }

    override fun stop() {
        Log.d(TAG, "SoundTask stop")
        ringtone?.stop()
    }
}