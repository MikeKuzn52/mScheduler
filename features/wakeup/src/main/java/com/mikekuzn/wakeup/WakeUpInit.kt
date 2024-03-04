package com.mikekuzn.wakeup

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.mikekuzn.mscheduler.AlarmUseCasesInter
import com.mikekuzn.mscheduler.RepositoryInter
import com.mikekuzn.mscheduler.TaskList
import com.mikekuzn.mscheduler.TaskListUpdater
import com.mikekuzn.mscheduler.entities.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val TAG = "mScheduler"

class WakeUpInit @Inject constructor(
    private val taskListM: TaskList,
    private val repository: RepositoryInter,
    private val alarmUseCases: AlarmUseCasesInter,
) {

    suspend fun launch() {
        Log.d(TAG, "WakeUpInit")
        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser
        val userPath = currentUser?.uid
        userPath?.let {
            CoroutineScope(Dispatchers.IO).launch {
                subscribe(userPath)
                alarmUseCases.setNextAlarm()
            }
        }
    }

    private suspend fun subscribe(userPath: String) = suspendCoroutine { continuation ->
        Log.d(TAG, "subscribe")
        repository.subscribe(
            userPath,
            object : TaskListUpdater(taskListM.getTaskList(), {}) {
                override fun addOrUpdate(task: Task) {
                    taskListM.getTaskList().add(task)
                }
                override fun after() {
                    continuation.resume(Unit)
                }
            }
        )
    }
}