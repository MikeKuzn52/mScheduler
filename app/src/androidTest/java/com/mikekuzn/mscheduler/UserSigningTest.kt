package com.mikekuzn.mscheduler

import android.os.Build
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.mikekuzn.mscheduler.dateTimePicker.DateTimePicker
import com.mikekuzn.mscheduler.dateTimePicker.DateTimePickerInter
import com.mikekuzn.mscheduler.dateTimePicker.LocalDateTimePickerProvider
import com.mikekuzn.mscheduler.presentation.MainActivity
import com.mikekuzn.mscheduler.presentation.RootWindow
import com.mikekuzn.mscheduler.ui.theme.MSchedulerTheme
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
//@Config(sdk = [Build.VERSION_CODES.R])
class UserSigningTest {

    @get:Rule
    val testRule = createAndroidComposeRule<MainActivity>()

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var dateTimePickerProvider: Provider<DateTimePicker>

    @Inject
    lateinit var signing: Signing

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun signingTest() {
        testRule.setContent {
            MSchedulerTheme {
                //https://developer.android.com/training/dependency-injection/hilt-testing
                //@TestInstallIn(
                CompositionLocalProvider(
                    LocalSigning provides signing,
                    LocalDateTimePickerProvider provides dateTimePickerProvider as Provider<DateTimePickerInter>
                ) {
                    RootWindow()
                }
            }
        }
        testRule.onNode(hasText("Enter Email"))
            .performClick()
            .performTextInput("TestMail@mail.ru")
        testRule.onNode(hasText("Enter Password"))
            .performClick()
            .performTextInput("TestPassword")
        testRule.onNode(hasText("Sign In"))
            .performClick()
        testRule.onNode(hasText("Here will be your notes and tasks"))
            .assertExists()
        //Thread.sleep(500)
    }
}
