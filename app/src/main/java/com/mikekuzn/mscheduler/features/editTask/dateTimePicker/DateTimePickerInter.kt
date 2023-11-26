package com.mikekuzn.mscheduler.features.editTask.dateTimePicker

import java.util.Calendar

interface DateTimePickerInter {
    fun showDataPicker(calendar: Calendar, pickerResult: (calendar: Calendar) -> Unit)
    fun showTimePicker(calendar: Calendar, pickerResult: (calendar: Calendar) -> Unit)
}