package com.mikekuzn.mscheduler

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.getValue
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

const val TAG = "mScheduler"

class FirebaseDB @AssistedInject constructor(
    @Assisted("FirebaseUserPath")
    private val userPath: String
) {
    private var dbRef: DatabaseReference
    private var listener: ValueEventListener? = null

    init {
        Log.d(TAG, "FirebaseDB init for userPath=$userPath")
        dbRef = FirebaseDatabase.getInstance().getReference(userPath)
    }

    fun subscribe(
        before: () -> Unit,
        add: (key: String, task: TaskData) -> Unit,
        after: () -> Unit
    ) {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                before()
                Log.d(TAG, "onDataChange count=${snapshot.children.count()}")
                for (data in snapshot.children) {
                    try {
                        val key: String = data.key ?: continue
                        val taskData: TaskData? = data.getValue<TaskData>()
                        Log.d(TAG, "onDataChange key=$key taskData=$taskData")
                        add(key, taskData!!)
                    } catch (e: Exception) {
                        Log.e(TAG, "onDataChange error and remove ${e.message} children=$data")
                        dbRef.child(data.key!!).removeValue()
                    }
                }
                after()
            }

            override fun onCancelled(error: DatabaseError) {
                // TODO("Not yet implemented")
            }
        }
        dbRef.addValueEventListener(listener!!)
    }

    fun unsubscribe() {
        listener?.let { dbRef.removeEventListener(it) }
    }

    suspend fun add(taskData: TaskData): String? {
        dbRef.push().apply {
            Log.d(TAG, "add taskData=$taskData")
            return suspendCoroutine { continuation ->
                setValue(taskData) { error, _ ->
                    continuation.resume(
                        if (error == null) key else null
                    )
                }
            }
        }
    }

    suspend fun delete(key: String): Boolean =
        suspendCoroutine { continuation ->
            dbRef.child(key).removeValue() { error, _ ->
                continuation.resume(error == null)
            }
        }


    suspend fun modify(key: String, newTask: TaskData): Boolean =
        suspendCoroutine { continuation ->
            dbRef.child(key).setValue(newTask) { error, _ ->
                continuation.resume(error == null)
            }
        }
}

@AssistedFactory
interface FirebaseDBFactory {
    fun create(@Assisted("FirebaseUserPath") userPath: String): FirebaseDB
}
