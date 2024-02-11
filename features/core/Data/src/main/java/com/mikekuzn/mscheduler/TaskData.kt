package com.mikekuzn.mscheduler

import android.graphics.Color

data class TaskData(
    val title: String = "",
    val description: String = "",
    val haveChild: Boolean = false,// TODO not yet implemented
    val parentKey: String? = null,// TODO not yet implemented
    val useTime: Boolean = true,
    var dataTime: Long = 0L,
    //var repeat: Repeat = Repeat.Once,
    val minutesBefore: List<Int> = listOf(0),// TODO not yet implemented
    val colorValue: Color = Color(),// TODO not yet implemented
    val checked: Boolean = true,
    val check: Boolean = false,
    val isSystemMelody: Boolean = true,
    //val melody: Uri? = null,// TODO not yet implemented
) {
    constructor() : this("")
}
