package com.mikekuzn.mscheduler.features.custom_list

import androidx.compose.foundation.gestures.awaitDragOrCancellation
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.drag
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUp
import androidx.compose.ui.input.pointer.positionChange
import kotlin.coroutines.cancellation.CancellationException

suspend fun PointerInputScope.gestureDetector(
    onStart: (Offset) -> Unit = { },
    onEnd: () -> Unit = { },
    onCancel: () -> Unit = { },
    onMove: (change: PointerInputChange, dragAmount: Offset) -> Unit = { _, _ -> },
    left: Float = 0f,
    right: Float,
) {
    awaitEachGesture {
        try {
            val down = awaitFirstDown(requireUnconsumed = false)
            val drag = awaitDragOrCancellation(down.id)
            if (drag != null && drag.position.x in left..right) {
                onStart.invoke(drag.position)
                if (drag(drag.id) {
                        onMove(it, it.positionChange())
                        it.consume()
                    }
                ) {
                    currentEvent.changes.forEach {
                        if (it.changedToUp()) it.consume()
                    }
                    onEnd()
                } else {
                    onCancel()
                }
            }
        } catch (c: CancellationException) {
            onCancel()
        }
    }
}