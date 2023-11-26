package com.mikekuzn.mscheduler.features.taskList

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.mikekuzn.mscheduler.domain.UseCasesInter
import com.mikekuzn.mscheduler.domain.entities.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    val handle: SavedStateHandle,
    private val useCases: UseCasesInter,
) : ViewModel(), TaskListViewModelInter {

    private var tmp = mutableStateListOf<Task>()

    init {
        for(i in 1..30) {
            tmp.add(Task("","Task $i"))
        }
    }

    override val taskList
        get() = tmp//useCases.getTaskList()

    override fun swap(from: Int, to: Int) {
        //useCases.swap(from, to)
        tmp.add(to, tmp.removeAt(from))

    }

    override fun addTask(newTask: Task) {
        useCases.addTask(newTask)
    }

    override fun setAsSubTask(index: Int) {
        //TODO("Not yet implemented")
    }

    override fun deleteTask(index: Int) {
        //useCases.deleteTask(index)
        tmp.removeAt(index)
    }

    override val isSigned get() = useCases.isSigned
}
