package com.mikekuzn.mscheduler.features.taskList

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

    override val taskList
        get() = useCases.getTaskList()

    override fun swap(from: Int, to: Int) {
        useCases.swap(from, to)
    }

    override fun addTask(newTask: Task) {
        useCases.addTask(newTask)
    }

    override fun setAsSubTask(index: Int) {
        //TODO("Not yet implemented")
    }

    override fun deleteTask(index: Int) {
        useCases.deleteTask(index)
    }

    override val isSigned get() = useCases.isSigned
}
