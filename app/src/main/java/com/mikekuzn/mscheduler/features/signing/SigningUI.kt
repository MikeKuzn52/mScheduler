package com.mikekuzn.mscheduler.features.signing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.mikekuzn.mscheduler.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SigningUI(
    signing: SigningInter = LocalSigning.current
) {
    var email by remember { mutableStateOf(signing.currentEmail) }
    var pass by remember { mutableStateOf("") }
    var showPass by remember { mutableStateOf(false) }
    val messageOrState = signing.messageOrState
    var timer by remember { mutableIntStateOf(60) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        if (signing.waiting) {
            Text(text = "Wait")
        } else {
            TextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.padding(10.dp),
                readOnly = signing.signed,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                maxLines = 1,
                label = { Text(text = stringResource(id = R.string.enterEmail)) },
            )
            if (!signing.signed) {
                TextField(
                    value = pass,
                    onValueChange = { pass = it },
                    modifier = Modifier.padding(10.dp),
                    maxLines = 1,
                    visualTransformation = if (showPass) VisualTransformation.None else PasswordVisualTransformation(),
                    label = { Text(text = stringResource(id = R.string.enterPass)) },
                )
                SwitchWithLabel(stringResource(id = R.string.showPass), showPass) { showPass = it }
                Button(onClick = { signing.signIn(email, pass) }) {
                    Text(text = stringResource(id = R.string.signIn))
                }
                Button(onClick = { signing.signUp(email, pass) }) {
                    Text(text = stringResource(id = R.string.signUp))
                }
            } else if (!signing.isEmailVerified) {
                LaunchedEffect(key1 = timer) {
                    if (timer == 60) {
                        signing.sendMailVerification()
                    }
                    delay(1_000)
                    signing.readState()
                    timer -= 1
                }
                Button(onClick = { signing.signOut() }) {
                    Text(text = stringResource(id = R.string.signOut))
                }
                Button(
                    onClick = { timer = 60 },
                    enabled = timer <= 0
                ) {
                    Text(text = stringResource(id = R.string.sendMailToConfirm))
                }
                if (timer in 1..59)
                    Text(text = timer.toString())
            }
            messageOrState?.let { Text(text = it) }
        }
    }
}

@Composable
private fun SwitchWithLabel(label: String, state: Boolean, onStateChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .clickable(onClick = { onStateChange(!state) })
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label)
        Spacer(modifier = Modifier.padding(start = 10.dp))
        Switch(
            checked = state,
            onCheckedChange = { onStateChange(it) },
        )
    }
}