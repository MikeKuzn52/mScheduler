package com.mikekuzn.mscheduler.features.taskList.draganddroplist

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.floor

enum class ScrollbarSelectionMode {
    Full,   // Enable selection in the whole scrollbar and thumb
    Thumb,  // Enable selection in the thumb
    Disabled    // Disable selection
}

enum class ScrollbarSelectionActionable {
    Always,     // Can select scrollbar always (when visible or hidden)
    WhenVisible,    // Can select scrollbar only when visible
}

@Composable
fun LazyColumnScrollbar(
    modifier: Modifier = Modifier,
    listState: LazyListState,
    rightSide: Boolean = true,
    alwaysShowScrollBar: Boolean = false,
    thickness: Dp = 6.dp,
    scrollBarPadding: Dp = 4.dp,
    thumbMinHeight: Float = 0.1f,
    thumbColor: Color = Color(0xFF2A59B6),
    thumbSelectedColor: Color = Color(0xFF5281CA),
    thumbShape: Shape = CircleShape,
    selectionMode: ScrollbarSelectionMode = ScrollbarSelectionMode.Thumb,
    selectionActionable: ScrollbarSelectionActionable = ScrollbarSelectionActionable.Always,
    hideDelayMillis: Int = 400,
    enabled: Boolean = true,
    indicatorContent: (@Composable (index: Int, maxIndex: Int, isThumbSelected: Boolean) -> Unit)? = null,
    content: @Composable () -> Unit
) {
    if (!enabled) content()
    else Box(modifier = modifier.padding(
        start = if (rightSide) 0.dp else scrollBarPadding + thickness,
        end = if (!rightSide) 0.dp else scrollBarPadding + thickness,
    )) {
        content()

        val firstVisibleItemIndex = remember { derivedStateOf { listState.firstVisibleItemIndex } }

        val coroutineScope = rememberCoroutineScope()

        var isSelected by remember { mutableStateOf(false) }

        var dragOffset by remember { mutableFloatStateOf(0f) }

        val reverseLayout by remember { derivedStateOf { listState.layoutInfo.reverseLayout } }

        val realFirstVisibleItem by remember {
            derivedStateOf {
                listState.layoutInfo.visibleItemsInfo.firstOrNull {
                    it.index == listState.firstVisibleItemIndex
                }
            }
        }

        val isStickyHeaderInAction by remember {
            derivedStateOf {
                val realIndex = realFirstVisibleItem?.index ?: return@derivedStateOf false
                val firstVisibleIndex = listState.layoutInfo.visibleItemsInfo.firstOrNull()?.index
                    ?: return@derivedStateOf false
                realIndex != firstVisibleIndex
            }
        }

        fun LazyListItemInfo.fractionHiddenTop(firstItemOffset: Int) =
            if (size == 0) 0f else firstItemOffset / size.toFloat()

        fun LazyListItemInfo.fractionVisibleBottom(viewportEndOffset: Int) =
            if (size == 0) 0f else (viewportEndOffset - offset).toFloat() / size.toFloat()

        val normalizedThumbSizeReal by remember {
            derivedStateOf {
                listState.layoutInfo.let {
                    if (it.totalItemsCount == 0)
                        return@let 0f

                    val firstItem = realFirstVisibleItem ?: return@let 0f
                    val firstPartial =
                        firstItem.fractionHiddenTop(listState.firstVisibleItemScrollOffset)
                    val lastPartial =
                        1f - it.visibleItemsInfo.last().fractionVisibleBottom(it.viewportEndOffset)

                    val realSize = it.visibleItemsInfo.size - if (isStickyHeaderInAction) 1 else 0
                    val realVisibleSize = realSize.toFloat() - firstPartial - lastPartial
                    realVisibleSize / it.totalItemsCount.toFloat()
                }
            }
        }

        val normalizedThumbSize by remember {
            derivedStateOf {
                normalizedThumbSizeReal.coerceAtLeast(thumbMinHeight)
            }
        }

        fun offsetCorrection(top: Float): Float {
            val topRealMax = (1f - normalizedThumbSizeReal).coerceIn(0f, 1f)
            if (normalizedThumbSizeReal >= thumbMinHeight) {
                return when {
                    reverseLayout -> topRealMax - top
                    else -> top
                }
            }

            val topMax = 1f - thumbMinHeight
            return when {
                reverseLayout -> (topRealMax - top) * topMax / topRealMax
                else -> top * topMax / topRealMax
            }
        }

        fun offsetCorrectionInverse(top: Float): Float {
            if (normalizedThumbSizeReal >= thumbMinHeight)
                return top
            val topRealMax = 1f - normalizedThumbSizeReal
            val topMax = 1f - thumbMinHeight
            return top * topRealMax / topMax
        }

        val normalizedOffsetPosition by remember {
            derivedStateOf {
                listState.layoutInfo.let {
                    if (it.totalItemsCount == 0 || it.visibleItemsInfo.isEmpty())
                        return@let 0f

                    val firstItem = realFirstVisibleItem ?: return@let 0f
                    val top = firstItem
                        .run { index.toFloat() + fractionHiddenTop(listState.firstVisibleItemScrollOffset) } / it.totalItemsCount.toFloat()
                    offsetCorrection(top)
                }
            }
        }

        fun setDragOffset(value: Float) {
            val maxValue = (1f - normalizedThumbSize).coerceAtLeast(0f)
            dragOffset = value.coerceIn(0f, maxValue)
        }

        fun setScrollOffset(newOffset: Float) {
            setDragOffset(newOffset)
            val totalItemsCount = listState.layoutInfo.totalItemsCount.toFloat()
            val exactIndex = offsetCorrectionInverse(totalItemsCount * dragOffset)
            val index: Int = floor(exactIndex).toInt()
            val remainder: Float = exactIndex - floor(exactIndex)

            coroutineScope.launch {
                listState.scrollToItem(index = index, scrollOffset = 0)
                val offset = realFirstVisibleItem
                    ?.size
                    ?.let { it.toFloat() * remainder }
                    ?: 0f
                listState.scrollBy(offset)
            }
        }

        fun totalItemsCount() = listState.layoutInfo.totalItemsCount

        val isInAction = listState.isScrollInProgress || isSelected || alwaysShowScrollBar

        val isInActionSelectable = remember { mutableStateOf(isInAction) }
        val durationAnimationMillis = 500
        LaunchedEffect(isInAction) {
            if (isInAction) {
                isInActionSelectable.value = true
            } else {
                delay(timeMillis = durationAnimationMillis.toLong() + hideDelayMillis.toLong())
                isInActionSelectable.value = false
            }
        }

        val alpha by animateFloatAsState(
            targetValue = if (isInAction) 1f else 0f,
            animationSpec = tween(
                durationMillis = if (isInAction) 75 else durationAnimationMillis,
                delayMillis = if (isInAction) 0 else hideDelayMillis
            ),
            label = "scrollbar alpha value"
        )

        val displacement by animateFloatAsState(
            targetValue = if (isInAction) (thickness + scrollBarPadding).value else 14f,
            animationSpec = tween(
                durationMillis = if (isInAction) 75 else durationAnimationMillis,
                delayMillis = if (isInAction) 0 else hideDelayMillis
            ),
            label = "scrollbar displacement value"
        )

        BoxWithConstraints(
            modifier = modifier
                .fillMaxWidth()
        ) {
            val maxHeightFloat = constraints.maxHeight.toFloat()
            ConstraintLayout(
                modifier = Modifier
                    .align(if (rightSide) Alignment.TopEnd else Alignment.TopStart)
                    .graphicsLayer(
                        translationX = with(LocalDensity.current) { (if (rightSide) displacement.dp else -displacement.dp).toPx() },
                        translationY = maxHeightFloat * normalizedOffsetPosition
                    )
            ) {
                val (box, cont) = createRefs()
                Box(
                    modifier = Modifier
                        .padding(
                            start = if (rightSide) 0.dp else scrollBarPadding,
                            end = if (!rightSide) 0.dp else scrollBarPadding,
                        )
                        .clip(thumbShape)
                        .width(thickness)
                        .fillMaxHeight(normalizedThumbSize)
                        .alpha(alpha)
                        .background(if (isSelected) thumbSelectedColor else thumbColor)
                        .constrainAs(box) {
                            if (rightSide) end.linkTo(parent.end)
                            else start.linkTo(parent.start)
                        }
                        .testTag(TestTagsScrollbar.scrollbar)
                )

                if (indicatorContent != null) {
                    Box(
                        modifier = Modifier
                            .alpha(alpha)
                            .constrainAs(cont) {
                                top.linkTo(box.top)
                                bottom.linkTo(box.bottom)
                                if (rightSide) end.linkTo(box.start)
                                else start.linkTo(box.end)
                            }
                            .testTag(TestTagsScrollbar.scrollbarIndicator),
                    ) {
                        indicatorContent(
                            index = firstVisibleItemIndex.value,
                            maxIndex = totalItemsCount(),
                            isThumbSelected = isSelected
                        )
                    }
                }
            }

            @Composable
            fun DraggableBar() = Box(
                modifier = Modifier
                    .align(if (rightSide) Alignment.TopEnd else Alignment.TopStart)
                    .width(scrollBarPadding * 2 + thickness)
                    .fillMaxHeight()
                    .draggable(
                        state = rememberDraggableState { delta ->
                            val displace = if (reverseLayout) -delta else delta // side effect ?
                            if (isSelected) {
                                setScrollOffset(dragOffset + displace / maxHeightFloat)
                            }
                        },
                        orientation = Orientation.Vertical,
                        enabled = selectionMode != ScrollbarSelectionMode.Disabled,
                        startDragImmediately = true,
                        onDragStarted = onDragStarted@{ offset ->
                            if (maxHeightFloat <= 0f) return@onDragStarted
                            val newOffset = when {
                                reverseLayout -> (maxHeightFloat - offset.y) / maxHeightFloat
                                else -> offset.y / maxHeightFloat
                            }
                            val currentOffset = when {
                                reverseLayout -> 1f - normalizedOffsetPosition - normalizedThumbSize
                                else -> normalizedOffsetPosition
                            }

                            when (selectionMode) {
                                ScrollbarSelectionMode.Full -> {
                                    if (newOffset in currentOffset..(currentOffset + normalizedThumbSize))
                                        setDragOffset(currentOffset)
                                    else
                                        setScrollOffset(newOffset)
                                    isSelected = true
                                }

                                ScrollbarSelectionMode.Thumb -> {
                                    if (newOffset in currentOffset..(currentOffset + normalizedThumbSize)) {
                                        setDragOffset(currentOffset)
                                        isSelected = true
                                    }
                                }

                                ScrollbarSelectionMode.Disabled -> Unit
                            }
                        },
                        onDragStopped = {
                            isSelected = false
                        }
                    )
                    .testTag(TestTagsScrollbar.scrollbarContainer)
            )

            if (
                when (selectionActionable) {
                    ScrollbarSelectionActionable.Always -> true
                    ScrollbarSelectionActionable.WhenVisible -> isInActionSelectable.value
                }
            ) {
                DraggableBar()
            }
        }
    }
}
internal object TestTagsScrollbar {
    const val scrollbar = "scrollbar"
    const val scrollbarContainer = "scrollbarContainer"
    const val scrollbarIndicator = "scrollbarIndicator"
}