package com.mikekuzn.mscheduler.data

import androidx.compose.ui.graphics.Color

data class TaskData(
    val title: String = "",
    val description: String = "",
    val childCount: Int = 0,// TODO not yet implemented
    val parentKey: String? = null,// TODO not yet implemented
    val useTime: Boolean = true,
    var dataTime: Long = 0L,
    //var repeat: Repeat = Repeat.Once,
    val minutesBefore: List<Int> = listOf(0),// TODO not yet implemented
    val colorValue: Color = Color.White,// TODO not yet implemented
    val checked: Boolean = true,
    val check: Boolean = false,
    val isSystemMelody: Boolean = true,
    //al melody: Uri? = null,// TODO not yet implemented
) {
    constructor() : this("")
}
