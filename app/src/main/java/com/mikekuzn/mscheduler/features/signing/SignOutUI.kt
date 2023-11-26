package com.mikekuzn.mscheduler.features.signing

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.mikekuzn.mscheduler.R

@Composable
fun SignOut(
    signing: SigningInter = LocalSigning.current
) {
    var openDialog by remember { mutableStateOf(false) }
    Image(
        painter = painterResource(R.drawable.i_logout),
        contentDescription = "Add new task",
        Modifier
            .clickable { openDialog = true }
    )

    if (openDialog) {
        AlertDialog(
            title = { Text(text = stringResource(id = R.string.signOutTitle)) },
            onDismissRequest = { openDialog = false },
            confirmButton = {
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Button(onClick = { openDialog = false }) {
                        Text(text = stringResource(id = R.string.signOutCancel))
                    }
                    Button(onClick = { signing.signOut() }
                    ) {
                        Text(text = stringResource(id = R.string.signOutConfirm))
                    }
                }
            }
        )
    }
}
