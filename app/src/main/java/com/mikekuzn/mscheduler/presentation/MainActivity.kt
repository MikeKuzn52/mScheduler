package com.mikekuzn.mscheduler.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.addCallback
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.mikekuzn.mscheduler.features.editTask.EditTaskViewModel
import com.mikekuzn.mscheduler.features.editTask.dateTimePicker.DateTimePickerInter
import com.mikekuzn.mscheduler.features.editTask.dateTimePicker.LocalDateTimePickerProvider
import com.mikekuzn.mscheduler.features.signing.LocalSigning
import com.mikekuzn.mscheduler.features.signing.SigningInter
import com.mikekuzn.mscheduler.ui.theme.MSchedulerTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import javax.inject.Provider

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var dateTimePickerProvider: Provider<DateTimePickerInter>

    @Inject
    lateinit var signing: SigningInter
    private val editTaskViewModel: EditTaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onBackPressedDispatcher.addCallback(this) {
            if (!editTaskViewModel.stopEdit()) {
                finish()
            }
        }
        setContent {
            MSchedulerTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CompositionLocalProvider(
                        LocalSigning provides signing,
                        LocalDateTimePickerProvider provides dateTimePickerProvider
                    ) {
                        RootWindow()
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        signing.unsubscribe()
    }
}
