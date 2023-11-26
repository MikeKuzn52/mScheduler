package com.mikekuzn.mscheduler.data

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

class FirebaseDB @AssistedInject constructor(
    @Assisted("FirebaseUserPath")
    private val userPath: String
) {
    private var dbRef: DatabaseReference
    private var listener: ValueEventListener? = null

    init {
        Log.d("***[", "FirebaseDB init for userPath=$userPath")
        dbRef = FirebaseDatabase.getInstance().getReference(userPath)
    }

    fun subscribe(add: (task: TaskData) -> Unit) {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (data in snapshot.children) {
                    val taskData: TaskData? = data.getValue<TaskData>()
                    Log.d("***[", "onDataChange taskData=$taskData")
                    taskData?.let { add(it) }
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // TODO("Not yet implemented")
            }
        }
        dbRef.addValueEventListener(listener!!)
    }

    fun add(taskData: TaskData): Boolean {
        dbRef.push().setValue(taskData)
        Log.d("***[", "onDataChange taskData=$taskData")
        return true
    }

    fun getNextKey() = dbRef.key

    fun unsubscribe() {
        listener?.let { dbRef.removeEventListener(it) }
    }
}

@AssistedFactory
interface FirebaseDBFactory {
    fun create(@Assisted("FirebaseUserPath") userPath: String): FirebaseDB
}