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


class RepositoryTest{

    private val dBFactory = mockk<FirebaseDBFactory>()
    private val dB = mockk<FirebaseDB>()
    private val testPath = "TestPath"
    private val testKey = "TestKey"
    private val updater = mockk<TaskListUpdaterInter>()
    private lateinit var repository: Repository

    @Before
    fun init() {
        repository = Repository(dBFactory)
        every { dBFactory.create(any()) } returns dB
        every { dB.subscribe(any(),any(),any()) } returns Unit
        every { dB.unsubscribe() } returns Unit
        coEvery { dB.add(any()) } returns testKey
        coEvery { dB.delete(any()) } returns true
        coEvery { dB.modify(any(),any()) } returns true
    }

    @Test
    fun subscribe() {
        repository.subscribe(testPath, updater)
        verify { dBFactory.create(any()) }
        verify { dB.subscribe(any(),any(),any()) }
    }

    @Test
    fun add() = runTest {
        repository.add(Task()) // test non Crash
        repository.subscribe(testPath, updater)
        repository.add(Task())
        coVerify { dB.add(any()) }
    }

    @Test
    fun unsubscribe() = runTest {
        repository.unsubscribe() // test non Crash
        repository.subscribe(testPath, updater)
        repository.unsubscribe()
        repository.add(Task())
        coVerify(exactly = 0) { dB.add(any()) }
    }

    @Test
    fun delete() = runTest {
        repository.delete(testKey) // test non Crash
        repository.subscribe(testPath, updater)
        val result = repository.delete(testKey)
        coVerify { dB.delete(testKey) }
        assertEquals(true, result)
    }

    @Test
    fun modify() = runTest {
        repository.modify(testKey, Task()) // test non Crash
        repository.subscribe(testPath, updater)
        val result = repository.modify(testKey, Task())
        coVerify { dB.modify(testKey, any()) }
        assertEquals(true, result)
    }
}