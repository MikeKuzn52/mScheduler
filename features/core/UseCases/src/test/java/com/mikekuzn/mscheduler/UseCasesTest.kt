package com.mikekuzn.mscheduler

import com.mikekuzn.mscheduler.entities.Task
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class UseCasesTest {

    private val repository = mockk<RepositoryInter>()
    private val alarmUpdater = mockk<AlarmUseCasesUpdateInter>()
    private val testPath = "TestPath"
    private val task = Task(useTime = true)
    private val taskList = TaskList()
    private var testKey = 0

    private fun getUseCases() =
        UseCases(taskList, repository, alarmUpdater)

    @Before
    fun init() {
        testKey = 0
        taskList.getTaskList().clear()
        every { repository.subscribe(any(), any()) } returns Unit
        every { repository.unsubscribe() } returns Unit
        coEvery { repository.add(any()) } answers { "TestKey ${testKey++}" }
        coEvery { repository.modify(any(), any()) } returns true
        coEvery { repository.delete(any()) } returns true
        every { alarmUpdater.updateForTask(any()) } returns Unit
        every { alarmUpdater.updateWhenDelete(any()) } returns Unit
    }

    @Test
    fun subscribe() {
        val useCases = getUseCases()
        useCases.setUserPath(testPath)
        verify { repository.subscribe(testPath, any()) }
        assertEquals(true, useCases.isSigned)
    }

    @Test
    fun unsubscribe() {
        val useCases = getUseCases()
        assertEquals(false, useCases.isSigned)
        useCases.clrUserPath() // test non Crash
        useCases.setUserPath(testPath)
        assertEquals(true, useCases.isSigned)
        useCases.clrUserPath()
        assertEquals(false, useCases.isSigned)

    }

    @Test
    fun `Add task and set timer`() = runTest {
        val useCases = getUseCases()
        useCases.addTask(task)
        verify { alarmUpdater.updateForTask(task) }
    }

    @Test
    fun `Add task and swap`() = runTest {
        val useCases = getUseCases()
        useCases.addTask(Task(description = "Task1"))
        useCases.addTask(Task(description = "Task2"))
        assertEquals("Task1", taskList.getTaskList()[0].description)
        assertEquals("Task2", taskList.getTaskList()[1].description)
        useCases.swap(0, 1)
        assertEquals("Task2", taskList.getTaskList()[0].description)
        assertEquals("Task1", taskList.getTaskList()[1].description)
        coVerify(exactly = 2) { repository.modify(any(), any()) }
        useCases.swap(0, 2) // test non Crash
    }

    @Test
    fun `Delete task`() = runTest {
        val useCases = getUseCases()
        useCases.addTask(task);
        assertEquals(1, taskList.getTaskList().size)
        useCases.deleteTask(1) // test non Crash
        useCases.deleteTask(0)
        assertEquals(0, taskList.getTaskList().size)
        verify { alarmUpdater.updateWhenDelete(task) }
    }

    @Test
    fun `Add task and setAsSubTask`() = runTest {
        val useCases = getUseCases()
        useCases.addTask(Task(description = "Task1"))
        useCases.addTask(Task(description = "Task2"))
        useCases.setAsSubTask(0)
        useCases.setAsSubTask(2) // test non Crash
        assertEquals(null, taskList.getTaskList()[0].parentKey)
        useCases.setAsSubTask(1)
        assertEquals("TestKey 0", taskList.getTaskList()[1].parentKey)
    }
}
