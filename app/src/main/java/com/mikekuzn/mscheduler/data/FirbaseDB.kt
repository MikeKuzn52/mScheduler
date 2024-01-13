package com.mikekuzn.mscheduler.data

/* Firebase rules:
{
  "rules": {
    "$uid": {
     ".read": "auth != null",
     ".write": "auth != null && auth.uid === $uid",
    }
  }
}*/

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

    fun subscribe(
        before: ()->Unit,
        add: (key: String, task: TaskData)->Unit,
        after: ()->Unit
    ) {
        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                before()
                Log.d("***[", "onDataChange count=${snapshot.children.count()}")
                for (data in snapshot.children) {
                    try {
                        val key: String = data.key ?: continue
                        val taskData: TaskData? = data.getValue<TaskData>()
                        Log.d("***[", "onDataChange key=$key taskData=$taskData")
                        add(key, taskData!!)
                    } catch (e: Exception) {
                        Log.e("***[", "onDataChange error and remove ${e.message} children=$data")
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

    fun add(taskData: TaskData): String? {
        dbRef.push().apply {
            Log.d("***[", "add taskData=$taskData")
            // TODO("make suspend  setValue(taskData, CompletionListener listener)")
            setValue(taskData)
            return key
        }
    }

    fun delete(key: String) {
        // TODO("make suspend  removeValue(CompletionListener listener)")
        dbRef.child(key).removeValue()
    }

    fun modify(key: String, newTask: TaskData) {
        // TODO("make suspend  setValue(Object value, CompletionListener listener)")
        dbRef.child(key).setValue(newTask)
    }
}

@AssistedFactory
interface FirebaseDBFactory {
    fun create(@Assisted("FirebaseUserPath") userPath: String): FirebaseDB
}
