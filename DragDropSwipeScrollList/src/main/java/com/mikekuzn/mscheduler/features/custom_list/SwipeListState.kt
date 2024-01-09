package com.mikekuzn.mscheduler.features.custom_list

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp

enum class SwipeState { NO_SWIPED, SUBTASK, DELETE }

val stepWith = 300.dp// TODO ????????????


@Composable
fun rememberSwipeListState(
    index: State<Int?>,
    onTree: (Int) -> Unit,
    onDelete: (Int) -> Unit,
): SwipeListState {
    return remember { SwipeListState(index, onTree, onDelete) }
}

class SwipeListState(
    private val index: State<Int?>,
    private val onTree: (Int) -> Unit,
    private val onDelete: (Int) -> Unit,
) {
    private var swipeDistance = 0.dp
    private var swipeState by mutableStateOf(SwipeState.NO_SWIPED)

    fun getSwipeState(index: Int) = if (index == this.index.value) swipeState else SwipeState.NO_SWIPED

    fun onSwipeStart(offset: Offset) {
        setOffset(offset = offset)
    }

    fun onSwipe(offset: Offset) {
        setOffset(offset = offset)
    }

    private fun setOffset(offset: Offset) {
        swipeDistance += offset.x.dp
        swipeState = when {
            swipeDistance < stepWith * 1 -> SwipeState.NO_SWIPED
            swipeDistance < stepWith * 2 -> SwipeState.SUBTASK
            else -> SwipeState.DELETE
        }
    }

    fun onSwipeEnd() {
        Log.d("***[", "onSwipeEnd index=${index.value} swipeState=$swipeState")
        index.value?.let {
            when (swipeState) {
                SwipeState.NO_SWIPED -> {}
                SwipeState.SUBTASK -> onTree(it)
                SwipeState.DELETE -> onDelete(it)
            }
        }
        onSwipeInterrupted()
    }

    fun onSwipeInterrupted() {
        swipeDistance = 0.dp
        swipeState = SwipeState.NO_SWIPED
    }

}