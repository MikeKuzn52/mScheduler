package com.mikekuzn.mscheduler.presentation

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mikekuzn.mscheduler.AlarmUseCasesInter
import com.mikekuzn.mscheduler.SoundTaskInter
import com.mikekuzn.mscheduler.entities.Task
import com.mikekuzn.mscheduler.service.AlarmService
import com.mikekuzn.mscheduler.ui.theme.MSchedulerTheme
import com.mikekuzn.resource.R
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.roundToInt

const val TAG = "mScheduler"

@AndroidEntryPoint
class AlarmActivity : ComponentActivity() {
    @Inject
    lateinit var alarmUseCases: AlarmUseCasesInter
    @Inject
    lateinit var soundTask: SoundTaskInter

    private lateinit var taskList: List<Task>
    private var taskIndex = -1
    private val currentTask: MutableState<Task?> = mutableStateOf(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        stopAlarmService()
        soundTask.stop()
        // TODO("Move string constants to separate file")
        val actionTime = intent?.extras?.getLong("ACTION_TIME")
        Log.d(TAG, "AlarmActivity onCreate actionTime=$actionTime ${intent?.extras}")
        taskList = alarmUseCases.getByTime(actionTime)
        alarmUseCases.setNextAlarm()
        setContent {
            MSchedulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    if (getNextTask()) {
                        Greeting(alarmUseCases, currentTask as State<Task?>, ::getNextTask)
                    }
                    else {
                        Text("Internal error. No events to show")
                    }
                }
            }
        }
    }

    private fun getNextTask() =
        if (++taskIndex >= taskList.size) {
            Log.d(TAG, "AlarmActivity no more events")
            finish()
            false
        } else {
            Log.d(TAG, "AlarmActivity NextTask $taskIndex")
            currentTask.value = taskList[taskIndex]
            true
        }

    private fun stopAlarmService() {
        val stopIntent = Intent(this, AlarmService::class.java)
        stopService(stopIntent)
    }
}

@Composable
fun Greeting(alarmUseCases: AlarmUseCasesInter, task: State<Task?>, getNext: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.alarmActivityColor)),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val context = LocalContext.current
        Text(
            modifier = Modifier.padding(10.dp),
            text = stringResource(id = R.string.alarmEvent),
            fontSize = dimensionResource(R.dimen.biggestFontSize).value.sp,
        )
        Text(
            text = task.value!!.title,
            textAlign = TextAlign.Center,
            fontSize = dimensionResource(R.dimen.headFontSize).value.sp,
        )
        val shake = remember { Animatable(0f) }
        LaunchedEffect(0) {
            var i = 0
            while (true) {
                i++
                if (i and 0x10 == 0) {
                    when (i % 2) {
                        0 -> shake.animateTo(15f, spring(stiffness = 40_000f))
                        else -> shake.animateTo(-15f, spring(stiffness = 40_000f))
                    }
                } else {
                    shake.animateTo(0f)
                }
            }
        }
        Button(
            onClick = {
                alarmUseCases.setReady(task.value!!)
                getNext()
            },
            modifier = Modifier
                .padding(10.dp)
                .offset { IntOffset(shake.value.roundToInt(), y = 0) }
                .rotate(-shake.value / 8)
        ) {
            Text(text = stringResource(id = R.string.finishEvent))
        }

        Button(onClick = {
            // TODO("Not yet implemented: alarmUseCases.postpone(task.value, ...)")
            Toast.makeText(context, "Not yet implemented", Toast.LENGTH_SHORT).show()
        }) {
            Text(text = stringResource(id = R.string.postponeEvent))
        }
    }
}