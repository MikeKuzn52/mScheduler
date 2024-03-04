package com.mikekuzn.mscheduler

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.mikekuzn.wakeup.WakeUpInit
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

const val TAG = "mScheduler"

@AndroidEntryPoint
class WakeupReceiver : BroadcastReceiver() {

    @Inject
    lateinit var initiator: WakeUpInit
    @Inject
    lateinit var  exceptionHandler: CoroutineExceptionHandler

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d(TAG, "WakeupReceiver ${intent?.action}")
        if (intent?.action == "android.intent.action.BOOT_COMPLETED") {
            // TODO("runBlocking?")
            runBlocking(exceptionHandler) {
                initiator.launch()
            }
        }
    }
}
