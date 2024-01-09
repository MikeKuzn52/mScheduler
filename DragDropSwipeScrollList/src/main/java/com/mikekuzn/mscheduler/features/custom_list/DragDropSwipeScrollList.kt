package com.mikekuzn.mscheduler.features.custom_list

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job

@Composable
fun <T> DragDropSwipeScrollList(
    mItems: List<T>,
    onSwap: (Int, Int) -> Unit,
    onTree: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier,
    scrollBarPadding: Dp = 2.dp,
    dragInductor: (@Composable (item: T, swipeState: SwipeState, modifier: Modifier) -> Unit)? = null,
    indicatorContent: (@Composable (index: Int, maxIndex: Int, isThumbSelected: Boolean) -> Unit)? = null,
    divider: @Composable () -> Unit = { Divider(color = Color.Gray) },
    showItem: @Composable (T) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val overScrollJob = remember { mutableStateOf<Job?>(null) }
    val dragDropListState = rememberDragDropListState(onSwap = onSwap)

    val swipeListState = rememberSwipeListState(
        index = dragDropListState.indexState,
        onTree = onTree,
        onDelete = onDelete,
    )
    var indicatorEnd = remember { 0f }

    LazyColumnScrollbar(
        listState = dragDropListState.lazyListState,
        modifier = modifier,
        scrollBarPadding = scrollBarPadding,
        indicatorContent = indicatorContent,
    ) {
        LazyColumn(
            modifier = Modifier
                .pointerInput(Unit) {
                    gestureDetector(
                        onStart = { offset ->
                            dragDropListState.onDragStart(offset)
                            swipeListState.onSwipeStart(offset)
                        },
                        onMove = { change, offset ->
                            change.consume()
                            dragDropListState.onDrag(offset, overScrollJob, scope)
                            swipeListState.onSwipe(offset)
                        },
                        onEnd = {
                            swipeListState.onSwipeEnd()
                            dragDropListState.onDragEnd()
                        },
                        onCancel = {
                            dragDropListState.onDragInterrupted()
                            swipeListState.onSwipeInterrupted()
                        },
                        right = indicatorEnd,
                    )
                },
            state = dragDropListState.lazyListState,
            reverseLayout = false,
        ) {
            itemsIndexed(mItems) { index, item ->
                val dragged = index == dragDropListState.indexState.value
                val swipeState = swipeListState.getSwipeState(index)
                val elevation by animateDpAsState(
                    if (dragged && swipeState == SwipeState.NO_SWIPED) 3.dp else 0.dp,
                    label = "D&D Animation"
                )
                val scale by animateFloatAsState(
                    if (dragged && swipeState == SwipeState.NO_SWIPED) 0.97F else 1F,
                    label = "D&D Animation"
                )
                val offsetY: Float =
                    if (dragged) dragDropListState.elementDisplacement ?: 0F else 0F
                Row(
                    modifier = Modifier
                        .graphicsLayer(translationY = offsetY)
                        .fillMaxWidth()
                        .scale(scale)
                        .shadow(elevation)
                ) {

                    if (dragInductor != null) {
                        dragInductor(
                            item,
                            swipeState,
                            Modifier
                                .onGloballyPositioned { coordinates ->
                                    indicatorEnd = coordinates.size.width.toFloat()
                                },
                        )
                    }
                    showItem(item)
                }
                divider()
            }
        }
    }
}


