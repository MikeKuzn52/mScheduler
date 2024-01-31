package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.alarmmanager.CustomAlarmManagerInter
import com.mikekuzn.mscheduler.entities.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class AlarmUseCasesTest {

    private val taskList = TaskList()
    private val alarmManager = mockk<CustomAlarmManagerInter>()
    private val timeGetter = mockk<GetCurrentTime>()
    private lateinit var alarmUseCases: AlarmUseCases
    private val taskListSameTime = listOf(
        Task(useTime = true, dataTime = 100L + 10 * 1000, minutesBefore = listOf(10)),
        Task(useTime = true, dataTime = 100L + 15 * 1000, minutesBefore = listOf(15))
    )

    @Before
    fun init_before() {
        every { timeGetter.execute() } returns 50
        every { alarmManager.writeAlarm(any(), any()) } returns Unit
        every { alarmManager.cancelAlarm(any()) } returns Unit
        taskList.getTaskList().clear()
        alarmUseCases = AlarmUseCases(
            taskList,
            alarmManager,
            timeGetter,
        )
    }

    @Test
    fun `Set timer`() {
        val dataTime = 100L + 5 * 1000
        val expectedTime = 100L
        alarmUseCases.updateForTask(
            Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(5))
        )
        verify { alarmManager.writeAlarm(any(), expectedTime) }
        verify(exactly = 0) { alarmManager.cancelAlarm(any()) }
    }

    @Test
    fun `Add two task and non cansel and set timer`() {
        val dataTime = 100L + 6 * 1000
        val expectedTime = 100L
        alarmUseCases.updateForTask(
            Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(6))
        )
        alarmUseCases.updateForTask(
            Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(5))
        )
        verify { alarmManager.writeAlarm(any(), expectedTime) }
        verify(exactly = 0) { alarmManager.cancelAlarm(any()) }
    }

    @Test
    fun `Add two task and cansel and set timer`() {
        val dataTime = 100L + 15 * 1000
        val expectedTime = 100L
        alarmUseCases.updateForTask(
            Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(10))
        )
        alarmUseCases.updateForTask(
            Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(15))
        )
        verify(atLeast = 1) { alarmManager.writeAlarm(any(), expectedTime) }
        verify(exactly = 2) { alarmManager.writeAlarm(any(), any()) }
        verify(exactly = 1) { alarmManager.cancelAlarm(any()) }
    }

    @Test
    fun `Add oldTime task`() {
        alarmUseCases.updateForTask(
            Task(useTime = true, dataTime = 0, minutesBefore = listOf(0))
        )
        alarmUseCases.updateForTask(
            Task(useTime = false, dataTime = 100, minutesBefore = listOf(0))
        )
        verify(exactly = 0) { alarmManager.writeAlarm(any(), any()) }
        verify(exactly = 0) { alarmManager.cancelAlarm(any()) }
    }

    @Test
    fun `Add two same tasks and non cansel and set timer`() {
        alarmUseCases.updateForTask(taskListSameTime[0])
        alarmUseCases.updateForTask(taskListSameTime[1])
        verify(exactly = 1) { alarmManager.writeAlarm(any(), any()) }
        verify(exactly = 0) { alarmManager.cancelAlarm(any()) }
    }

    @Test
    fun `Add two sames task and get hash`() {
        taskList.getTaskList().addAll(taskListSameTime)
        val actualTaskList = alarmUseCases.getByHash(100L.hashCode())
        assertEquals(actualTaskList.size, 2)
        assertEquals(actualTaskList[0], taskListSameTime[0])
        assertEquals(actualTaskList[1], taskListSameTime[1])
    }
}
