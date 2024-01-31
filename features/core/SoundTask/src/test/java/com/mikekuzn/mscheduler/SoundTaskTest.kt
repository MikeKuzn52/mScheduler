package com.mikekuzn.mscheduler

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import androidx.test.platform.app.InstrumentationRegistry
import com.mikekuzn.mscheduler.entities.Task
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SoundTaskTest {

    private lateinit var soundTask: SoundTask
    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val uriAlarm = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
    private val uriRingtone = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
    private val ringtone = mockk<Ringtone>()
    private lateinit var task: Task

    @Before
    fun init() {
        soundTask = SoundTask(context)
        mockkStatic(RingtoneManager::class)
        every { ringtone.play() } returns Unit
        every { ringtone.stop() } returns Unit
        task = Task(isSystemMelody = true)
    }


    @Test
    fun `play TYPE_ALARM`() {
        every { RingtoneManager.getRingtone(context, uriAlarm) } returns ringtone
        soundTask.execute(task)
        verify(exactly = 1) { ringtone.play() }
    }

    @Test
    fun `play TYPE_RINGTONE`() {
        every { RingtoneManager.getRingtone(context, uriAlarm) } returns null
        every { RingtoneManager.getRingtone(context, uriRingtone) } returns ringtone
        soundTask.execute(task)
        verify(exactly = 1) { ringtone.play() }
    }

    @Test
    fun `play melody`() {
        val uri = mockk<Uri>()
        task = Task(isSystemMelody = false, melody = uri)
        every { RingtoneManager.getRingtone(context, uri) } returns ringtone
        soundTask.execute(task)
        verify(exactly = 1) { ringtone.play() }
    }

    @Test
    fun `stop playing`() {
        every { RingtoneManager.getRingtone(any(), any()) } returns ringtone
        soundTask.execute(task)
        soundTask.stop()
        verify(exactly = 1) { ringtone.play() }
        verify(exactly = 1) { ringtone.stop() }
    }

    @Test
    fun `stop before playing`() {
        every { RingtoneManager.getRingtone(any(), any()) } returns ringtone
        soundTask.execute(task)
        soundTask.execute(task)
        verify(exactly = 1) { ringtone.stop() }
    }
}