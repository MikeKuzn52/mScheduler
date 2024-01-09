package com.mikekuzn.mscheduler.domain.entities

import android.net.Uri
import androidx.compose.ui.graphics.Color
import java.util.Calendar

data class Task(
    val title: String = "",
    val description: String = "",
    val childCount: Int = 0,// TODO not yet implemented
    val parentKey:String? = null,// TODO not yet implemented
    val useTime: Boolean = true,
    var dataTime: Long = Calendar.getInstance().timeInMillis,
    var repeat: Repeat = Repeat.Once,
    val minutesBefore: List<Int> = listOf(0),// TODO not yet implemented
    val colorValue: Color = Color.White,// TODO not yet implemented
    val checked: Boolean = true,
    val check: Boolean = false,
    val isSystemMelody: Boolean = true,
    val melody: Uri? = null,// TODO not yet implemented
) {
    var key: String? = null
}