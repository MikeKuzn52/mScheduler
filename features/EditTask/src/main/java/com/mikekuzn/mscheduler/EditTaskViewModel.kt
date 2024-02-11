package com.mikekuzn.mscheduler

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mikekuzn.mscheduler.entities.Repeat
import com.mikekuzn.mscheduler.entities.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditTaskViewModel @Inject constructor(
    val handle: SavedStateHandle,
    private val useCases: UseCasesInter,
) : ViewModel(), EditTaskViewModelInter {

    private val editTaskM = mutableStateOf<Task?>(null)
    override val editTask
        get() = editTaskM as State<Task?>

    override fun edit(task: Task?) {
        editTaskM.value = task ?: Task()
    }

    override fun stopEdit(): Boolean {
        return (editTask.value != null).also { editTaskM.value = null }
    }

    override fun setTitle(t: String) {
        editTaskM.value = editTask.value!!.copy(title = t)
    }

    override fun setDescription(d: String) {
        editTaskM.value = editTask.value!!.copy(description = d)
    }

    override fun invertUseTime() {
        editTaskM.value = editTask.value!!.copy(useTime = !editTask.value!!.useTime)
    }

    override fun changeDataTime(newDataTime: Long) {
        editTaskM.value = editTask.value!!.copy(dataTime = newDataTime)
    }

    override fun setRepeat(repeat: Repeat) {
        editTaskM.value = editTask.value!!.copy(repeat = repeat)
    }

    override fun addTask(newTask: Task) {
        viewModelScope.launch {
            useCases.addTask(newTask)
        }
    }
}
