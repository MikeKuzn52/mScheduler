package com.mikekuzn.mscheduler

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.mikekuzn.mscheduler.entities.Task
import javax.inject.Inject

class UseCases @Inject constructor(
    private val taskList: TaskList,
    private val repository: RepositoryInter,
    private val alarmUpdater: AlarmUseCasesUpdateInter,
) : UseCasesInter {

    private var savedUserPath by mutableStateOf<String?>(null)

    private var taskListM = taskList.getTaskList()

    override fun setUserPath(userPath: String) {
        Log.d("***[", "setUserPath $userPath")
        if (savedUserPath != userPath) {
            savedUserPath?.run { throw Exception("Internal error. Add UserPath") }
            savedUserPath = userPath
            repository.subscribe(
                userPath,
                object : TaskListUpdater(taskListM, ::localDeleteTask) {
                    override fun addOrUpdate(newTask: Task) {
                        super.addOrUpdate(newTask)
                        taskListM.find { it.key == newTask.key }?.let {
                            // TODO("check hash sum and change")
                        } ?: run {
                            alarmUpdater.updateForTask(newTask)
                            taskListM.add(newTask)

                        }
                    }
                    override fun after() {
                        super.after()
                        checkTasks()
                    }
                }
            )
        }
    }

    override fun clrUserPath() {
        this.savedUserPath = null
        repository.unsubscribe()
        taskListM.clear()
    }

    override fun getTaskList(): SnapshotStateList<Task> = taskListM

    override fun swap(from: Int, to: Int) {
        if (from == to) return
        // swap key values to swap on firebase
        taskListM[to].key = taskListM[from].key.also { taskListM[from].key = taskListM[to].key }
        modifyTask(to)
        modifyTask(from)
        // swap on local array
        taskListM.add(to, taskListM.removeAt(from))
    }

    // Add form UI
    override fun addTask(newTask: Task) {
        alarmUpdater.updateForTask(newTask)
        // if newTask.key == null it is local task
        newTask.key ?: let {
            val newKey = repository.add(newTask)
            Log.d("***[", "addTask key='${newTask.key}->$newKey")
            newTask.key = newKey
            if (newKey.isNullOrEmpty()) {
                // TODO newKey == null -> Toast or anything also and repeat to write
            } else if (!taskListM.any { it.key == newKey }) {
                taskListM.add(newTask)
            }
        }
    }

    override fun deleteTask(index: Int) {
        // TODO("make suspend")
        repository.delete(taskListM[index].key)
        localDeleteTask(index)
    }

    private fun localDeleteTask(index: Int) {
        alarmUpdater.updateWhenDelete(taskListM[index])
        taskListM.removeAt(index)
    }

    override fun modifyTask(index: Int) {
        if (taskListM[index].key.isNullOrEmpty()) {
            Log.e("***[", "modifyTask N$index key='${taskListM[index].key}")
            // TODO Toast or anything also
            return
        }
        // TODO("make suspend")
        repository.modify(taskListM[index].key!!, taskListM[index])
    }

    override fun setAsSubTask(index: Int) {
        if (index >= 1) {
            taskListM[index].parentKey = taskListM[index - 1].key
            taskListM[index - 1].haveChild = true
            modifyTask(index)
            modifyTask(index - 1)
        }
    }

    private fun checkTasks() {
        // TODO("not yet implemented")
    }

    override val isSigned get() = this.savedUserPath != null
}
