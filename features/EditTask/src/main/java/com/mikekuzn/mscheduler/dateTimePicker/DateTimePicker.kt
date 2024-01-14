package com.mikekuzn.mscheduler.dateTimePicker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.runtime.compositionLocalOf
import dagger.hilt.android.qualifiers.ActivityContext
import java.util.Calendar
import javax.inject.Inject
import javax.inject.Provider

class DateTimePicker @Inject constructor(@ActivityContext private val activityContext: Context) :
    DateTimePickerInter {

    override fun showDataPicker(calendar: Calendar, pickerResult: (calendar: Calendar) -> Unit) {
        DatePickerDialog(
            activityContext, {_, year : Int, month: Int, day: Int ->
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, month)
                calendar.set(Calendar.DAY_OF_MONTH, day)
                pickerResult(calendar)
            }, calendar[Calendar.YEAR], calendar[Calendar.MONTH], calendar[Calendar.DAY_OF_MONTH]
        ).show()
    }

    override fun showTimePicker(calendar: Calendar, pickerResult: (calendar: Calendar) -> Unit) {
        TimePickerDialog(
            activityContext, {_, hour : Int, minute: Int ->
                calendar.set(Calendar.HOUR_OF_DAY, hour)
                calendar.set(Calendar.MINUTE, minute)
                pickerResult(calendar)
            }, calendar[Calendar.HOUR_OF_DAY], calendar[Calendar.MINUTE], true
        ).show()
    }
}

val LocalDateTimePickerProvider = compositionLocalOf<Provider<DateTimePickerInter>>{ error("DateTimePicker is not provided")}
