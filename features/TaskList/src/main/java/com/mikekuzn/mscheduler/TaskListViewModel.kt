package com.mikekuzn.mscheduler

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikekuzn.mscheduler.entities.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskListViewModel @Inject constructor(
    val handle: SavedStateHandle,
    private val useCases: UseCasesInter,
    private val exceptionHandler: CoroutineExceptionHandler,
) : ViewModel(), TaskListViewModelInter {

    override val taskList
        get() = useCases.getTaskList()

    override fun swap(from: Int, to: Int) {
        viewModelScope.launch(exceptionHandler) {
            useCases.swap(from, to)
        }
    }

    override fun addTask(newTask: Task) {
        viewModelScope.launch(exceptionHandler) {
            useCases.addTask(newTask)
        }
    }

    override fun setAsSubTask(index: Int) {
        viewModelScope.launch(exceptionHandler) {
            useCases.setAsSubTask(index)
        }
    }

    override fun deleteTask(index: Int) {
        viewModelScope.launch(exceptionHandler) {
            useCases.deleteTask(index)
        }
    }

    override val isSigned get() = useCases.isSigned
}
