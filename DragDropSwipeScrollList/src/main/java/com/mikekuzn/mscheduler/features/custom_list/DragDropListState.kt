package com.mikekuzn.mscheduler.features.custom_list

import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun rememberDragDropListState(
    lazyListState: LazyListState = rememberLazyListState(),
    onSwap: (Int, Int) -> Unit
): DragDropListState {
    return remember { DragDropListState(lazyListState = lazyListState, onSwap = onSwap) }
}

class DragDropListState(
    val lazyListState: LazyListState,
    private val onSwap: (Int, Int) -> Unit
) {
    private var draggedDistance by mutableFloatStateOf(0f)
    private var initiallyDraggedElement by mutableStateOf<LazyListItemInfo?>(null)
    var indexState = mutableStateOf<Int?>(null)
    private val initialOffsets: Pair<Int, Int>?
        get() = initiallyDraggedElement?.let {
            Pair(it.offset, it.offsetEnd)
        }
    val elementDisplacement: Float?
        get() = indexState.value
            ?.let {
                lazyListState.getVisibleItemInfoFor(absolute = it)
            }
            ?.let {
                item -> (initiallyDraggedElement?.offset ?: 0f).toFloat() + draggedDistance - item.offset
            }

    private val currentElement: LazyListItemInfo?
        get() = indexState.value?.let {
            lazyListState.getVisibleItemInfoFor(absolute = it)
        }

    private var overScrollJob by mutableStateOf<Job?>(null)

    fun onDragStart(offset: Offset) {
        lazyListState.layoutInfo.visibleItemsInfo
            .firstOrNull { item ->
                offset.y.toInt() in item.offset..(item.offset + item.size)
            }?.also {
                indexState.value = it.index
                initiallyDraggedElement = it
            }
    }

    fun onDragEnd() {
        onDragInterrupted()
    }
    fun onDragInterrupted() {
        draggedDistance = 0f
        indexState.value = null
        initiallyDraggedElement = null
        overScrollJob?.cancel()
    }

    fun onDrag(
        offset: Offset,
        overScrollJob: MutableState<Job?>,
        scope: CoroutineScope
    ) {
        setOffset(offset = offset)

        if (overScrollJob.value?.isActive == true)
            return

        checkForOverScroll()
            .takeIf { it != 0f }
            ?.let {
                overScrollJob.value = scope.launch {
                    lazyListState.scrollBy(it)
                }
            } ?: run { overScrollJob.value?.cancel() }
    }

    private fun setOffset(offset: Offset) {
        draggedDistance += offset.y

        initialOffsets?.let { (topOffset, bottomOffset) ->
            val startOffset = topOffset + draggedDistance
            val endOffset = bottomOffset + draggedDistance

            currentElement?.let { hovered ->
                lazyListState.layoutInfo.visibleItemsInfo
                    .filterNot { item ->
                        item.offsetEnd < startOffset || item.offset > endOffset || hovered.index == item.index
                    }
                    .firstOrNull { item ->
                        val delta = startOffset - hovered.offset
                        when {
                            delta > 0 -> (endOffset > item.offsetEnd)
                            else -> (startOffset < item.offset)
                        }
                    }?.also { item ->
                        indexState.value?.let { index ->
                            onSwap.invoke(index, item.index)
                        }
                        indexState.value = item.index
                    }
            }
        }
    }

    private fun checkForOverScroll(): Float {
        return initiallyDraggedElement?.let {
            val startOffset = it.offset + draggedDistance
            val endOffset = it.offsetEnd + draggedDistance

            return@let when {
                draggedDistance > 0 -> (endOffset - lazyListState.layoutInfo.viewportEndOffset).takeIf { diff ->
                    diff > 0
                }
                draggedDistance < 0 -> (startOffset - lazyListState.layoutInfo.viewportStartOffset).takeIf { diff ->
                    diff < 0
                }
                else -> null
            }
        } ?: 0f
    }
}

fun LazyListState.getVisibleItemInfoFor(absolute: Int): LazyListItemInfo? {
    return this.layoutInfo.visibleItemsInfo.getOrNull(absolute - this.layoutInfo.visibleItemsInfo.first().index)
}

val LazyListItemInfo.offsetEnd: Int
    get() = this.offset + this.size






















