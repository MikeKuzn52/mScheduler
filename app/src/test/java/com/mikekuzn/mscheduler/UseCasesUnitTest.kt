package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.alarmmanager.CustomAlarmManagerInter
import com.mikekuzn.mscheduler.data.Repository
import com.mikekuzn.mscheduler.domain.GetCurrentTime
import com.mikekuzn.mscheduler.domain.UseCases
import com.mikekuzn.mscheduler.domain.UseCasesInter
import com.mikekuzn.mscheduler.domain.entities.Task
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.eq
import org.mockito.Mockito.anyInt
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.atLeastOnce
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`

class UseCasesUnitTest {

    private val timeGetter: GetCurrentTime = mock()
    private val repository: Repository = mock()
    private val alarmManager: CustomAlarmManagerInter = mock()
    private lateinit var useCases: UseCasesInter
    private val taskListSameTime = listOf(
        Task(useTime = true, dataTime = 100L + 10*1000, minutesBefore = listOf(10)),
        Task(useTime = true, dataTime = 100L + 15*1000, minutesBefore = listOf(15))
    )

    @Before
    fun init_before() {
        `when`(timeGetter.execute()).thenReturn(50)
        `when`(alarmManager.writeAlarm(anyInt(), anyLong())).then {  }
        `when`(alarmManager.cancelAlarm(anyInt())).then {  }
        useCases = UseCases(
            alarmManager = alarmManager,//AlarmManagerStab(),
            getCurrentTime = timeGetter,
            repository = repository
        )
    }

    @Test
    fun `Add task and set timer`() {
        val dataTime = 100L + 5 * 1000
        val expectedTime = 100L
        useCases.addTask(Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(5)))
        verify(alarmManager, times(1)).writeAlarm(anyInt(), eq(expectedTime))
        verify(alarmManager, times(0)).cancelAlarm(anyInt())
    }
    @Test
    fun `Add two task and non cansel and set timer`() {
        val dataTime = 100L + 6 * 1000
        val expectedTime = 100L
        useCases.addTask(Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(6)))
        useCases.addTask(Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(5)))
        verify(alarmManager, times(1)).writeAlarm(anyInt(), eq(expectedTime))
        verify(alarmManager, times(0)).cancelAlarm(anyInt())
    }
    @Test
    fun `Add two task and cansel and set timer`() {
        val dataTime = 100L + 15 * 1000
        val expectedTime = 100L
        useCases.addTask(Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(10)))
        useCases.addTask(Task(useTime = true, dataTime = dataTime, minutesBefore = listOf(15)))
        verify(alarmManager, atLeastOnce()).writeAlarm(anyInt(), eq(expectedTime))
        verify(alarmManager, times(2)).writeAlarm(anyInt(), anyLong())
        verify(alarmManager, times(1)).cancelAlarm(anyInt())
    }
    @Test
    fun `Add oldTime task`() {
        useCases.addTask(Task(useTime = true, dataTime = 0, minutesBefore = listOf(0)))
        useCases.addTask(Task(useTime = false, dataTime = 100, minutesBefore = listOf(0)))
        verify(alarmManager, times(0)).writeAlarm(anyInt(), anyLong())
        verify(alarmManager, times(0)).cancelAlarm(anyInt())
    }
    @Test
    fun `Add two same tasks and non cansel and set timer`() {
        useCases.addTask(taskListSameTime[0])
        useCases.addTask(taskListSameTime[1])
        verify(alarmManager, times(1)).writeAlarm(anyInt(), anyLong())
        verify(alarmManager, times(0)).cancelAlarm(anyInt())
    }
    @Test
    fun `Add two sames task and get hash`() {
        useCases.addTask(taskListSameTime[0])
        useCases.addTask(taskListSameTime[1])
        val actualTaskList = useCases.getByHash(100L.hashCode())
        assertEquals(actualTaskList.size, 2)
        assertEquals(actualTaskList[0], taskListSameTime[0])
        assertEquals(actualTaskList[1], taskListSameTime[1])
    }
}