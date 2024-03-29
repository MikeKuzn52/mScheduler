package com.mikekuzn.mscheduler.entities

import android.graphics.Color
import android.net.Uri
import java.util.Calendar

data class Task(
    val title: String = "",
    val description: String = "",
    var haveChild: Boolean = false, // TODO not yet implemented
    var parentKey:String? = null, // TODO not yet implemented
    val useTime: Boolean = true,
    var dataTime: Long = Calendar.getInstance().timeInMillis,
    var repeat: Repeat = Repeat.Once,
    val minutesBefore: List<Int> = listOf(0), // TODO not yet implemented
    val colorValue: Color = Color(), // TODO not yet implemented
    val checked: Boolean = true,
    val check: Boolean = false,
    val isSystemMelody: Boolean = true,
    val melody: Uri? = null, // TODO not yet implemented
) {
    var key: String? = null
}