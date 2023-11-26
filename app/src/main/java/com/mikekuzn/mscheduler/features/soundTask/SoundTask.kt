package com.mikekuzn.mscheduler.features.soundTask

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.util.Log
import com.mikekuzn.mscheduler.domain.entities.Task
import dagger.hilt.android.qualifiers.ActivityContext
import javax.inject.Inject

class SoundTask @Inject constructor(@ActivityContext private val context: Context): SoundTaskInter {
    override fun soundTask(task: Task) {
        Log.d("***[", "Play ${task.title}")
        val ringtone: Ringtone? = if (task.isSystemMelody) {
            RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ) ?: RingtoneManager.getRingtone(
                context,
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
            )
        } else {
            RingtoneManager.getRingtone(
                context,
                task.melody
            )
        }
        ringtone?.play()
    }
}